package com.jspjavaplsql.board.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface BoardService {

    /**     * 게시판 목록 조회 (페이징)     */
    List<Map<String, Object>> selectBoardList(int page, int pageSize);

    /**     * 게시글 전체 건수     */
    int selectBoardCount();
    
    // 상세보기 메서드 추가
    Map<String, Object> selectBoardDetail(int boardId);
    
    void updateViewCount(int boardId); // 추가
    List<Map<String, Object>> selectBoardFileList(int boardId); // 추가
    Map<String, Object> selectFileInfo(int fileId); // 다운로드용 추가
    
    // 게시글 내용(map)과 파일(uploadFile)을 동시에 처리
    void insertBoard(Map<String, Object> map, MultipartFile[] uploadFile) throws Exception;
    
    // 기존 메서드들 아래에 추가
    void deleteBoard(int boardId) throws Exception;
    
    void updateBoard(Map<String, Object> map, MultipartHttpServletRequest request, int[] delFiles) throws Exception;
}
