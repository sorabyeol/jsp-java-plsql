package com.jspjavaplsql.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jspjavaplsql.board.dao.BoardDAO;

@Service
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
        // 상세 조회 시 조회수 증가와 상세 내용 조회를 한 번에 처리
        boardDAO.updateViewCount(boardId);
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
}