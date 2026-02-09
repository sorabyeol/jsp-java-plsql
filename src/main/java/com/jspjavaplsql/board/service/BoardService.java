package com.jspjavaplsql.board.service;

import java.util.List;
import java.util.Map;

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
}
