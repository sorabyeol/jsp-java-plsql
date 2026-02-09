package com.jspjavaplsql.board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jspjavaplsql.board.service.BoardService;


@Controller
@RequestMapping("/board") // 모든 메서드 주소 앞에 /board가 자동으로 붙음
public class BoardController {

    @Autowired
    private BoardService boardService;

    @RequestMapping("/boardListAjax.do")
    @ResponseBody
    public Map<String, Object> boardListAjax(
            @RequestParam(defaultValue = "1") int page) {

        int pageSize = 10;

        List<Map<String, Object>> list =
                boardService.selectBoardList(page, pageSize);

        int totalCount = boardService.selectBoardCount();
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("currentPage", page);
        result.put("totalPage", totalPage);

        return result;
    }
    
    // 1. 목록 화면(JSP)을 열어주는 역할
    @RequestMapping("/boardList.do")
    public String boardList() {
        // 리턴값 "board_list"는 /WEB-INF/views/board_list.jsp를 의미합니다.
        return "board_list"; 
    }
    
    
    @RequestMapping("/boardDetail.do")
    public String boardDetail(@RequestParam("boardId") int boardId, Model model) {
        // 1. 조회수 증가 (지난번에 만든 것)
        boardService.updateViewCount(boardId);

        // 2. 게시글 상세 조회
        Map<String, Object> board = boardService.selectBoardDetail(boardId);
        
        // 3. 첨부파일 리스트 조회 (추가된 부분)
        List<Map<String, Object>> fileList = boardService.selectBoardFileList(boardId);

        // 4. 화면(JSP)으로 데이터 전달
        model.addAttribute("board", board);
        model.addAttribute("fileList", fileList); 

        return "board_detail";
    }
    
    @RequestMapping("/download.do")
    public void fileDownload(@RequestParam("fileId") int fileId, 
                             HttpServletResponse response,
                             HttpServletRequest request) throws Exception {
        
        // 1. DB에서 파일 정보 조회 (실제 저장된 이름, 원본 이름 등)
        Map<String, Object> fileInfo = boardService.selectFileInfo(fileId);
        String storedName = (String) fileInfo.get("STORED_NAME");
        String originalName = (String) fileInfo.get("ORIGINAL_NAME");
        String filePath = (String) fileInfo.get("FILE_PATH");

        // 2. 파일 객체 생성
        File file = new File(filePath, storedName);
        
        // 3. 파일 이름 한글 깨짐 방지 처리
        String userAgent = request.getHeader("User-Agent");
        String encodedName;
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            encodedName = URLEncoder.encode(originalName, "UTF-8").replaceAll("\\+", "%20");
        } else {
            encodedName = new String(originalName.getBytes("UTF-8"), "ISO-8859-1");
        }

        // 4. 응답 헤더 설정 (다운로드 창이 뜨게 함)
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedName + "\"");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setContentLength((int) file.length());

        // 5. 파일 읽어서 브라우저로 전송 (스트림)
        // commons-io 라이브러리를 사용하면 아주 간단하게 처리 가능합니다.
        byte[] fileByte = org.apache.commons.io.FileUtils.readFileToByteArray(file);
        
        response.getOutputStream().write(fileByte);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }
}
