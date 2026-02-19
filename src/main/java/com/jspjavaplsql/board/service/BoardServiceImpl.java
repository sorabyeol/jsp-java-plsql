package com.jspjavaplsql.board.service;

import java.io.File; // File 클래스 임포트
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
    
 /* direct sql 방식
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
*/

    @Override
    public List<Map<String, Object>> selectBoardList(int page, int pageSize) {
        // 1. 페이징 계산
        int startNum = (page - 1) * pageSize + 1;
        int endNum = page * pageSize;

        // 2. 파라미터와 결과(OUT)를 담을 Map 생성
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("startNum", startNum);
        paramMap.put("endNum", endNum);

        // 3. 프로시저 호출 
        // (주의: boardMapper.selectBoardList의 리턴값은 의미가 없으므로 무시합니다.)
        boardDAO.selectBoardList(paramMap);

        // 4. XML의 #{resultCursor, mode=OUT...} 설정에 의해 
        // 결과 리스트가 paramMap의 "resultCursor" 키에 자동으로 담겨 있습니다.
        List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get("resultCursor");

        // 5. 결과 반환 (만약 결과가 null이면 빈 리스트 반환)
        return (list != null) ? list : new ArrayList<>();
    }

    @Override
    public int selectBoardCount() {
        // 1. OUT 파라미터를 받아올 빈 Map 생성
        Map<String, Object> paramMap = new HashMap<>();

        // 2. 프로시저 호출
        boardDAO.selectBoardCount(paramMap);

        // 3. XML의 #{totalCount, mode=OUT...} 설정에 의해 
        // 건수가 paramMap의 "totalCount" 키에 담겨 돌아옵니다.
        Object result = paramMap.get("totalCount");
        
        // 4. 안전하게 숫자형으로 변환하여 반환
        return (result != null) ? Integer.parseInt(String.valueOf(result)) : 0;
    }

  
    
/* direct sql 방식
    @Override
    public Map<String, Object> selectBoardDetail(int boardId) {
        return boardDAO.selectBoardDetail(boardId);
    }
*/
    
    @Override
    public Map<String, Object> selectBoardDetail(int boardId) {
        // 1. 프로시저에 넘길 파라미터 맵 생성
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("boardId", boardId);

        // 2. 프로시저 호출 (XML의 CALL SP_BOARD_DETAIL 실행)
        boardDAO.selectBoardDetail(paramMap);

        // 3. 커서로 받아온 결과 리스트 꺼내기
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) paramMap.get("resultCursor");

        // 4. 상세조회는 결과가 1개이므로 첫 번째 Map 반환
        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        }
        return null;
    }
    
/* 간단한 조회나 파일 정보는 생산성을 위해 일반 SQL로 섞어서 쓰는 경우가 아주 많음, 아래 3개 */
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
  
    /****************************************/ 
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