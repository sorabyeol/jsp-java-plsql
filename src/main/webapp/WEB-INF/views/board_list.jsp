<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 목록</title>

<style>
    body { font-family: 'Malgun Gothic', sans-serif; font-size: 14px; color: #333; margin: 20px; }
    
    /* 헤더 영역 */
    .board-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }
    .board-header h3 { margin: 0; font-size: 22px; color: #444; }

    /* 글쓰기 버튼 */
    .btn-write {
        padding: 8px 16px;
        background-color: #4CAF50;
        color: white;
        border-radius: 4px;
        text-decoration: none;
        font-weight: bold;
        font-size: 14px;
        transition: background 0.3s;
    }
    .btn-write:hover { background-color: #45a049; }

    /* 테이블 스타일 */
    table { width: 100%; border-collapse: collapse; table-layout: fixed; } /* fixed로 너비 고정 */
    th, td { border: 1px solid #ddd; padding: 12px 8px; text-align: center; }
    th { background-color: #f8f9fa; font-weight: bold; color: #555; }
    
    /* 테이블 너비 배분 */
    th:nth-child(1) { width: 60px; }  /* 번호 */
    th:nth-child(2) { width: auto; }  /* 제목 */
    th:nth-child(3) { width: 100px; } /* 작성자 */
    th:nth-child(4) { width: 80px; }  /* 조회수 */
    th:nth-child(5) { width: 150px; } /* 작성일 */

    /* 제목 왼쪽 정렬 및 말줄임표 처리 */
    td.title { text-align: left; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    td.title a { color: #333; text-decoration: none; }
    td.title a:hover { color: #007bff; text-decoration: underline; }

    /* 마우스 오버 시 행 색상 변경 */
    tbody tr:hover { background-color: #f1f1f1; }

    /* 페이징 */
    .paging { margin-top: 20px; text-align: center; }
    .paging a, .paging strong {
        display: inline-block;
        padding: 5px 10px;
        margin: 0 2px;
        border: 1px solid #ddd;
        text-decoration: none;
        color: #333;
        border-radius: 3px;
        cursor: pointer;
    }
    .paging a:hover { background-color: #eee; }
    .paging strong { background-color: #4CAF50; color: white; border-color: #4CAF50; }
</style>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
let currentPage = 1;

$(document).ready(function () {
    loadList(1);
});

function loadList(page) {
    currentPage = page;
    $.ajax({
        url: 'boardListAjax.do',
        type: 'POST',
        dataType: 'json',
        data: { page: page },
        success: function (result) {
            drawTable(result.list);
            drawPaging(result.currentPage, result.totalPage);
        },
        error: function () {
            alert('목록 조회 중 오류가 발생했습니다.');
        }
    });
}

function drawTable(list) {
    let html = '';
    if (!list || list.length === 0) {
        html += '<tr><td colspan="5">등록된 게시글이 없습니다.</td></tr>';
    } else {
        list.forEach(row => {
            html += '<tr>';
            html += '<td>' + row.BOARD_ID + '</td>';
            html += '<td class="title">';
            html += '    <a href="boardDetail.do?boardId=' + row.BOARD_ID + '">' + row.TITLE + '</a>';
            html += '</td>';
            html += '<td>' + row.WRITER + '</td>';
            html += '<td>' + row.VIEW_COUNT + '</td>';
            html += '<td>' + (row.CREATED_DATE || '-') + '</td>';
            html += '</tr>';
        });
    }
    $('#boardBody').html(html);
}

function drawPaging(currentPage, totalPage) {
    let html = '';
    for (let i = 1; i <= totalPage; i++) {
        if (i === currentPage) {
            html += '<strong>' + i + '</strong>';
        } else {
            html += '<a onclick="loadList(' + i + ')">' + i + '</a>';
        }
    }
    $('#paging').html(html);
}
</script>

<script>
    // 페이지 로드 시 Controller에서 보낸 메시지가 있으면 alert 창을 띄움
    window.onload = function() {
        var message = "${msg}";
        if (message !== "") {
            alert(message);
        }
    }
</script>

</head>

<body>

<div class="board-header">
    <h3>jsp-java-plsql 게시판 목록</h3>
    <a href="${pageContext.request.contextPath}/board/boardWrite.do" class="btn-write">글쓰기</a>
</div>

<table>
    <thead>
        <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>조회수</th>
            <th>작성일</th>
        </tr>
    </thead>
    <tbody id="boardBody">
        </tbody>
</table>

<div id="paging" class="paging">
    </div>

</body>
</html>