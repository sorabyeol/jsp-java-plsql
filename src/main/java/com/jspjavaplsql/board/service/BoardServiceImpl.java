package com.jspjavaplsql.board.service;

import java.io.File; // File 클래스 임포트
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

    @Override
    public List<Map<String, Object>> selectBoardList(int page, int pageSize) {
        int start = (page - 1) * pageSize;
        Map<String, Object> param = new HashMap<>();
        param.put("start", start);
        param.put("pageSize", pageSize);
        return boardDAO.selectBoardList(param);
    }

    @Override
    public int selectBoardCount() {
        return boardDAO.selectBoardCount();
    }

    @Override
    public Map<String, Object> selectBoardDetail(int boardId) {
        return boardDAO.selectBoardDetail(boardId);
    }

    @Override
    public void updateViewCount(int boardId) {
        boardDAO.updateViewCount(boardId);
    }

    @Override
    public List<Map<String, Object>> selectBoardFileList(int boardId) {
        return boardDAO.selectBoardFileList(boardId);
    }

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
    
    @Override
    @Transactional
    public void deleteBoard(int boardId) throws Exception {
        List<Map<String, Object>> fileList = boardDAO.selectBoardFileList(boardId);
        
        if (fileList != null) {
            for (Map<String, Object> fileInfo : fileList) {
                String filePath = (String) fileInfo.get("FILE_PATH");
                String storedName = (String) fileInfo.get("STORED_NAME");
                if (filePath != null && storedName != null) {
                    File file = new File(filePath, storedName);
                    if (file.exists()) {
                        if (!file.delete()) throw new Exception("서버 파일 삭제 실패");
                    }
                }
            }
        }
        boardDAO.deleteBoardFiles(boardId);
        boardDAO.deleteBoard(boardId);
    }
    
 // BoardServiceImpl.java
    @Override
    @Transactional
    public void updateBoard(Map<String, Object> map, MultipartHttpServletRequest request, int[] delFiles) throws Exception {
        // 1. 게시글 본문 수정
        boardDAO.updateBoard(map);
        
        // 2. 선택된 파일 삭제 처리 (추가된 로직)
        if(delFiles != null) {
            for(int fileId : delFiles) {
                Map<String, Object> fileInfo = boardDAO.selectFileInfo(fileId);
                if(fileInfo != null) {
                    File file = new File((String)fileInfo.get("FILE_PATH"), (String)fileInfo.get("STORED_NAME"));
                    if(file.exists()) file.delete();
                    boardDAO.deleteFile(fileId); // DB에서 파일 정보 삭제
                }
            }
        }
        
        // 3. 새 파일 업로드 처리
        List<MultipartFile> uploadFiles = request.getFiles("uploadFile");
        int boardId = Integer.parseInt(String.valueOf(map.get("BOARD_ID")));
        if(uploadFiles != null && !uploadFiles.isEmpty()) {
            saveFiles(boardId, uploadFiles.toArray(new MultipartFile[0]));
        }
    }

    // 내부 파일 저장 공통 메서드
    private void saveFiles(int boardId, MultipartFile[] uploadFiles) throws Exception {
        String filePath = "C:\\workspace\\upload\\board\\";
        File dir = new File(filePath);
        if(!dir.exists()) dir.mkdirs();

        for (MultipartFile file : uploadFiles) {
            if (file != null && !file.isEmpty()) {
                String originalName = file.getOriginalFilename();
                String storedName = UUID.randomUUID().toString().replaceAll("-", "") + "_" + originalName;
                
                file.transferTo(new File(filePath + storedName));
                
                Map<String, Object> fileMap = new HashMap<>();
                fileMap.put("BOARD_ID", boardId);
                fileMap.put("ORIGINAL_NAME", originalName);
                fileMap.put("STORED_NAME", storedName);
                fileMap.put("FILE_PATH", filePath);
                fileMap.put("FILE_SIZE", file.getSize());
                
                boardDAO.insertBoardFile(fileMap);
            }
        }
    }
}