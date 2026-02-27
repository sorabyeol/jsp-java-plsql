package com.jspjavaplsql.board.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.jspjavaplsql.board.dao.BoardDAO;

@Service("boardService")
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardDAO boardDAO;

    // 1. 게시글 목록 조회 (프로시저 호출)
    @Override
    public List<Map<String, Object>> selectBoardList(int page, int pageSize) {
        int startNum = (page - 1) * pageSize + 1;
        int endNum = page * pageSize;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("startNum", startNum);
        paramMap.put("endNum", endNum);

        boardDAO.selectBoardList(paramMap);

        List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get("resultCursor");
        return (list != null) ? list : new ArrayList<>();
    }

    // 2. 게시글 전체 개수 조회 (프로시저 호출)
    @Override
    public int selectBoardCount() {
        Map<String, Object> paramMap = new HashMap<>();
        boardDAO.selectBoardCount(paramMap);
        Object result = paramMap.get("totalCount");
        return (result != null) ? Integer.parseInt(String.valueOf(result)) : 0;
    }

    // 3. 게시글 상세 조회 (프로시저 호출)
    @Override
    public Map<String, Object> selectBoardDetail(int boardId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("boardId", boardId);

        boardDAO.selectBoardDetail(paramMap);

        List<Map<String, Object>> resultList = (List<Map<String, Object>>) paramMap.get("resultCursor");
        return (resultList != null && !resultList.isEmpty()) ? resultList.get(0) : null;
    }

    // 4. 조회수 증가 (일반 SQL)
    @Override
    public void updateViewCount(int boardId) {
        boardDAO.updateViewCount(boardId);
    }

    // 5. 파일 목록 조회 (일반 SQL)
    @Override
    public List<Map<String, Object>> selectBoardFileList(int boardId) {
        return boardDAO.selectBoardFileList(boardId);
    }

    // 6. 개별 파일 정보 조회 (일반 SQL)
    @Override
    public Map<String, Object> selectFileInfo(int fileId) {
        return boardDAO.selectFileInfo(fileId);
    }

    @Override
    @Transactional
    public void insertBoard(Map<String, Object> map, MultipartFile[] uploadFiles) throws Exception {
        boardDAO.insertBoard(map);
        int boardId = Integer.parseInt(String.valueOf(map.get("BOARD_ID")));
        saveFiles(boardId, uploadFiles);
    }

    // 8. 게시글 삭제 (물리 파일 삭제 포함)
    @Override
    @Transactional
    public void deleteBoard(int boardId) throws Exception {
        // DB에서 파일 목록 먼저 조회
        List<Map<String, Object>> fileList = boardDAO.selectBoardFileList(boardId);
        
        // 실제 서버 파일 삭제
        if (fileList != null) {
            for (Map<String, Object> fileInfo : fileList) {
                deletePhysicalFile((String)fileInfo.get("FILE_PATH"), (String)fileInfo.get("STORED_NAME"));
            }
        }
        
        // DB 데이터 삭제 (파일 정보 -> 게시글 순)
        boardDAO.deleteBoardFiles(boardId);
        boardDAO.deleteBoard(boardId);
    }

    // 9. 게시글 수정 (파일 수정/삭제/추가 포함)
    @Override
    @Transactional
    public void updateBoard(Map<String, Object> map, MultipartHttpServletRequest request, int[] delFiles) throws Exception {
        // 본문 수정
        boardDAO.updateBoard(map);
        
        // 선택한 기존 파일 삭제 처리
        if (delFiles != null) {
            for (int fileId : delFiles) {
                Map<String, Object> fileInfo = boardDAO.selectFileInfo(fileId);
                if (fileInfo != null) {
                    deletePhysicalFile((String)fileInfo.get("FILE_PATH"), (String)fileInfo.get("STORED_NAME"));
                    boardDAO.deleteFile(fileId);
                }
            }
        }
        
        // 새 파일 추가 업로드 처리
        List<MultipartFile> uploadFiles = request.getFiles("uploadFile");
        if (uploadFiles != null && !uploadFiles.isEmpty()) {
            // map에 담겨있는 BOARD_ID를 활용
            int boardId = Integer.parseInt(String.valueOf(map.get("BOARD_ID")));
            saveFiles(boardId, uploadFiles.toArray(new MultipartFile[0]));
        }
    }

    // [공통] 서버 하드디스크 파일 삭제 로직
    private void deletePhysicalFile(String filePath, String storedName) {
        if (filePath != null && storedName != null) {
            File file = new File(filePath, storedName);
            if (file.exists()) file.delete();
        }
    }

    // [공통] 파일 저장 및 DB 정보 기록 (프로시저 활용)
    private void saveFiles(int boardId, MultipartFile[] uploadFiles) throws Exception {
        String filePath = "C:\\workspace\\upload\\board\\";
        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdirs();

        for (MultipartFile file : uploadFiles) {
            if (file != null && !file.isEmpty()) {
                String originalName = file.getOriginalFilename();
                String storedName = UUID.randomUUID().toString().replaceAll("-", "") + "_" + originalName;
                
                // 1) 서버 저장
                file.transferTo(new File(filePath + storedName));
                
                // 2) DB 정보 파라미터 맵 구성
                Map<String, Object> fileMap = new HashMap<>();
                fileMap.put("BOARD_ID", boardId);
                fileMap.put("ORIGINAL_NAME", originalName);
                fileMap.put("STORED_NAME", storedName);
                fileMap.put("FILE_PATH", filePath);
                fileMap.put("FILE_SIZE", file.getSize());
                
                // 3) 파일 저장 프로시저 호출
                boardDAO.insertBoardFile(fileMap);
            }
        }
    }
}