<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 목록</title>

<style>
    body { font-family: Arial; font-size: 14px; }
    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
    th { background-color: #f2f2f2; }
    td.title { text-align: left; }

    .paging {
        margin-top: 15px;
        text-align: center;
    }
    .paging a {
        margin: 0 5px;
        cursor: pointer;
        text-decoration: none;
    }
    .paging strong {
        margin: 0 5px;
        color: red;
    }
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
        data: {
            page: page
        },
        success: function (result) {
            drawTable(result.list);
            drawPaging(result.currentPage, result.totalPage);
        },
        error: function () {
            alert('목록 조회 실패');
        }
    });
}

function drawTable(list) {
    let html = '';

    if (!list || list.length === 0) {
        html += '<tr>';
        html += '<td colspan="5">게시글이 없습니다.</td>';
        html += '</tr>';
    } else {
        for (let i = 0; i < list.length; i++) {
            let row = list[i];
            html += '<tr>';
            html += '<td>' + row.BOARD_ID + '</td>';
            html += '<td class="title">';
            html += '<a href="boardDetail.do?boardId=' + row.BOARD_ID + '">';
            html += row.TITLE + '</a></td>';
            html += '<td>' + row.WRITER + '</td>';
            html += '<td>' + row.VIEW_COUNT + '</td>';
            html += '<td>' + row.CREATED_DATE + '</td>';
            html += '</tr>';
        }
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

</head>

<body>

<h2>게시판 목록</h2>

<table>
    <thead>
        <tr>
            <th>No</th>
            <th>제목</th>
            <th>작성자</th>
            <th>조회수</th>
            <th>작성일</th>
        </tr>
    </thead>
    <tbody id="boardBody">
        <!-- Ajax로 채워짐 -->
    </tbody>
</table>

<div id="paging" class="paging"></div>

</body>
</html>
