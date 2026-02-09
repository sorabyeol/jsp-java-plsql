package com.jspjavaplsql.board.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BoardDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    public List<Map<String, Object>> selectBoardList(Map<String, Object> param) {
        return sqlSession.selectList(
            "board.selectBoardList", param
        );
    }

    public int selectBoardCount() {
        return sqlSession.selectOne(
            "board.selectBoardCount"
        );
    }
    
    public Map<String, Object> selectBoardDetail(int boardId) {
        return sqlSession.selectOne("board.selectBoardDetail", boardId);
    }
    
    public void updateViewCount(int boardId) {
        sqlSession.update("board.updateViewCount", boardId);
    }
    
    // 게시글에 포함된 파일 리스트 조회
    public List<Map<String, Object>> selectBoardFileList(int boardId) {
        return sqlSession.selectList("board.selectBoardFileList", boardId);
    }

    // 파일 1개 정보 조회 (다운로드용)
    public Map<String, Object> selectFileInfo(int fileId) {
        return sqlSession.selectOne("board.selectFileInfo", fileId);
    }
}
