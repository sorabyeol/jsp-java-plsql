<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세보기</title>
<style>
    /* 전체 레이아웃 너비 고정 */
    body { width: 600px; }

    /* 제목 스타일 */
    h3 { font-size: 22px; color: #444; margin-top: 20px; }

    /* 1. 테이블 디자인 */
    table { width: 100%; border-collapse: collapse; }
    th, td { border: 1px solid #ccc; padding: 10px; font-size: 14px; }
    th { background: #eee; width: 100px; text-align: center; }
    .content { height: 200px; vertical-align: top; line-height: 1.5; }

    /* 2. 파일 목록 영역 */
    .file-list-area { width: 100%; margin-top: 15px; margin-bottom: 20px; }
    .file-list-area h4 { margin: 10px 0; font-size: 15px; border-bottom: 1px solid #ddd; padding-bottom: 5px; }
    .file-list-area ul { list-style: none; padding: 0; margin: 0; }
    .file-list-area li { 
        padding: 5px 0; 
        border-bottom: 1px solid #eee; 
        display: flex; 
        justify-content: space-between; 
        align-items: center;
    }
    
    /* 다운로드 버튼 소형화 */
    .btn-download-small { padding: 2px 8px; font-size: 12px; cursor: pointer; }

    /* 3. 하단 버튼 영역 (글쓰기 화면 스타일과 통일) */
    .btn-container {
        display: flex;
        justify-content: flex-end; /* 우측 정렬 */
        gap: 8px; /* 버튼 사이 간격 */
        margin-top: 20px;
    }

    /* 공통 버튼 스타일 */
    .btn {
        padding: 8px 18px;
        font-size: 14px;
        font-weight: bold;
        border-radius: 4px;
        border: 1px solid #ccc;
        cursor: pointer;
        transition: 0.2s;
        text-decoration: none;
        display: inline-block;
    }

    /* 목록 버튼 (회색계열) */
    .btn-list { background-color: #f7f7f7; color: #333; }
    .btn-list:hover { background-color: #e9e9e9; }

    /* 삭제 버튼 (빨간색 계열) */
    .btn-delete { 
        background-color: #ff4d4d; 
        color: white; 
        border: 1px solid #ff3333; 
    }
    .btn-delete:hover { background-color: #cc0000; }

</style>
</head>

<body>
    <h3>게시물 상세</h3>
	
    <table>
        <tr><th>제목</th><td>${board.TITLE}</td></tr>
        <tr><th>작성자</th><td>${board.WRITER}</td></tr>
        <tr><th>작성일</th><td>${board.CREATED_DATE}</td></tr>
        <tr><th>조회수</th><td>${board.VIEW_COUNT}</td></tr>
        <tr><th>내용</th><td class="content">${board.CONTENT}</td></tr>
    </table>
    
    <div class="file-list-area">
        <h4>첨부파일</h4>
        <c:choose>
            <c:when test="${not empty fileList}">
                <ul>
                    <c:forEach var="file" items="${fileList}">
                        <li>
                            <span style="font-size: 13px;">
                                📄 <strong>${file.ORIGINAL_NAME}</strong> 
                                <span style="color:gray;">(${file.FILE_SIZE} bytes)</span>
                            </span>
                            <a href="${pageContext.request.contextPath}/board/download.do?fileId=${file.FILE_ID}">
                                <button type="button" class="btn-download-small">다운로드</button>
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <p style="font-size:13px; color:gray;">첨부파일 없음</p>
            </c:otherwise>
        </c:choose>
    </div>
    
    <div class="btn-container">
        <button type="button" class="btn btn-list" onclick="location.href='boardList.do'">목록으로</button>
        <button type="button" class="btn btn-update" onclick="location.href='updateBoard.do?boardId=${board.BOARD_ID}'">수정하기</button>
        <button type="button" class="btn btn-delete" onclick="deleteBoard(${board.BOARD_ID});">삭제하기</button>
    </div>
    
    <script>
    function deleteBoard(boardId) {
        if(confirm("정말로 이 게시글을 삭제하시겠습니까?\n서버의 실제 파일도 함께 삭제됩니다.")) {
            location.href = "${pageContext.request.contextPath}/board/deleteBoard.do?boardId=" + boardId;
        }
    }
    </script>

    <script>
    // 컨트롤러에서 보낸 'msg'가 있다면 알림창을 띄웁니다.
    var msg = "${msg}";
    if(msg != "") {
        alert(msg);
    }
    </script>
    
</body>
</html>