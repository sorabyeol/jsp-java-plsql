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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    
   // 1. 글쓰기 화면(JSP)을 열어주는 매핑
    @RequestMapping("/boardWrite.do")
    public String boardWrite() {
        // WEB-INF/views/board_write.jsp를 찾아갑니다.
        return "board_write";
    }

    // 2. 글 등록 버튼을 눌렀을 때 데이터를 받는 매핑
    @RequestMapping("/insertBoard.do")
    public String insertBoard(@RequestParam Map<String, Object> map, 
                              @RequestParam("uploadFile") MultipartFile[] uploadFile) throws Exception {
        
        // 사용자가 입력한 제목, 작성자, 내용 등은 'map'에 담깁니다.
        // 첨부한 파일은 'uploadFile' 객체에 담깁니다.
        
        // Service단에 저장을 요청합니다.
        boardService.insertBoard(map, uploadFile);
        
        // 저장이 끝나면 다시 목록 화면으로 보냅니다.
        return "redirect:/board/boardList.do";
    }
    
    @RequestMapping("/deleteBoard.do")
    public String deleteBoard(@RequestParam("boardId") int boardId, RedirectAttributes rttr) {
        try {
            boardService.deleteBoard(boardId);
            // 성공 메시지 전달
            rttr.addFlashAttribute("msg", "삭제 완료 되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            // 실패 메시지 및 원인 전달
            rttr.addFlashAttribute("msg", "삭제 실패되었습니다. 실패원인 : " + e.getMessage());
        }
        
        return "redirect:/board/boardList.do";
    }
    
    
    // 수정 화면으로 이동
    @RequestMapping("/updateBoard.do")
    public String updateBoard(@RequestParam("boardId") int boardId, Model model) throws Exception {
        Map<String, Object> board = boardService.selectBoardDetail(boardId);
        List<Map<String, Object>> fileList = boardService.selectBoardFileList(boardId);
        
        model.addAttribute("board", board);
        model.addAttribute("fileList", fileList);
        
        return "board_update"; // JSP는 views 바로 아래 있으니 파일명만!
    }

    @RequestMapping("/updateBoardAction.do")
    public String updateBoardAction(@RequestParam Map<String, Object> map, 
                                    @RequestParam(value="delFiles", required=false) int[] delFiles,
                                    MultipartHttpServletRequest request, 
                                    RedirectAttributes rttr) {
        try {
            // 서비스 호출 시 delFiles 파라미터 추가
            boardService.updateBoard(map, request, delFiles);
            rttr.addFlashAttribute("msg", "수정이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("msg", "수정 실패!");
        }
        return "redirect:/board/boardDetail.do?boardId=" + map.get("BOARD_ID");
    }
}
