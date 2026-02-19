package com.jspjavaplsql.board.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("boardDAO")
public class BoardDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    // 1. 목록 조회 (selectOne으로 변경하여 프로시저 실행에 집중)
    public void selectBoardList(Map<String, Object> param) {
        // 프로시저는 OUT 파라미터로 값을 채워주므로 selectOne이 더 안정적입니다.
        sqlSession.selectOne("board.selectBoardList", param);
    }

    // 2. 전체 개수 조회
    public void selectBoardCount(Map<String, Object> param) {
        sqlSession.selectOne("board.selectBoardCount", param);
    }
    
/* direct sql 방식   
    // 3. 상세 조회
    public Map<String, Object> selectBoardDetail(int boardId) {
        return sqlSession.selectOne("board.selectBoardDetail", boardId);
    }
*/
    // 3. 상세 조회 (수정 후)
    public void selectBoardDetail(Map<String, Object> paramMap) {
        // 프로시저 호출 시에는 리턴값을 받지 않고, 
        // 파라미터로 넘긴 paramMap 안에 결과가 담기도록 합니다.
        sqlSession.selectOne("board.selectBoardDetail", paramMap);
    }
    
    // 4. 조회수 증가
    public void updateViewCount(int boardId) {
        sqlSession.update("board.updateViewCount", boardId);
    }
    
    // 5. 파일 목록 조회
    public List<Map<String, Object>> selectBoardFileList(int boardId) {
        return sqlSession.selectList("board.selectBoardFileList", boardId);
    }

    // 6. 파일 단일 정보 조회 (다운로드)
    public Map<String, Object> selectFileInfo(int fileId) {
        return sqlSession.selectOne("board.selectFileInfo", fileId);
    }
    
    // 7. 게시글 등록
    public void insertBoard(Map<String, Object> map) {
        sqlSession.insert("board.insertBoard", map);
    }

    // 8. 파일 정보 등록
    public void insertBoardFile(Map<String, Object> fileMap) {
        sqlSession.insert("board.insertBoardFile", fileMap);
    }
    
    // 9. 파일 삭제 (DB 데이터 삭제)
    public void deleteBoardFiles(int boardId) {
        sqlSession.delete("board.deleteBoardFiles", boardId);
    }

    // 10. 게시글 삭제
    public void deleteBoard(int boardId) {
        sqlSession.delete("board.deleteBoard", boardId);
    }
    
    // 11. 게시글 수정 (에러 해결 포인트!)
    public void updateBoard(Map<String, Object> map) throws Exception {
        // sqlSession 변수를 사용해서 update 메서드를 호출해야 합니다.
        sqlSession.update("board.updateBoard", map); 
    }
    
    public void deleteFile(int fileId) {
        sqlSession.delete("board.deleteFile", fileId);
    }
}