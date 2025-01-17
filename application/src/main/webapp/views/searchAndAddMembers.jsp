<%@page import="com.hsbc.meets.entity.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="e"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/meetingroommanagement/css/style.css">
    <title>Add members</title>
</head>

<body id="page-container">
<%
    User user = (User)request.getSession().getAttribute("user");
    pageContext.setAttribute("user", user);
    
    int capacity = (int) request.getAttribute("capacity");
    pageContext.setAttribute("capacity" , capacity);
   
%>
    <header>
        <!-- Navbar -->
        <nav class="navbar navbar-expand-lg bg-dark" style="height: 8vh">
            <div class="container">
                <!-- Logo -->
                <img src="/meetingroommanagement/resources/images/logo.png" height="30" alt="" />
                <h4 class="text-white ms-4 my-auto">HMeets</h4>
                <!-- Logo -->

                <!-- Menu button -->
                <button class="navbar-toggler" type="button" data-mdb-toggle="collapse" data-mdb-target="#navbarButtons" aria-controls="navbarButtons" aria-expanded="true" aria-label="Toggle navigation">
                    <img src="/meetingroommanagement/resources/images/icon_menu.png" height="22" alt="" class="me-1" />
                </button>
                <!-- Menu button -->

                <!-- Nav Items -->
                <div class="collapse navbar-collapse align-items-center" id="navbarButtons">
                    <div class="me-auto"></div>
                    <!-- <small class="text-info me-4">Link</small> -->
                    <ul class="align-nav-item">
                        <img src="/meetingroommanagement/resources/images/icon_user.png" height="16" alt="" class="me-1" />
                        <small class="text-white me-4">Hi! ${user.name})</small>
                    </ul>
                    <ul class="align-nav-item">
                        <a href="login?op=logout">
                            <button type="button" class="btn btn-outline-info" data-mdb-ripple-color="dark">
                                Logout
                            </button>
                        </a>
                    </ul>
                    <!-- </div> -->
                </div>
                <!-- Nav Items -->
            </div>
        </nav>
        <!-- Navbar -->
    </header>

    <main id="content-wrap">
        <div class="container-fluid">
            <div class="row d-flex justify-content-center align-items-center h-100">
                <div class="col-10 col-sm-8 col-md-9 col-lg-6 col-xl-5 offset-xl-1">
                    <!--Welcome Text-->
                    <div class="d-flex flex-row align-items-center justify-content-center justify-content-lg-start my-4">
                        <h4 class="text-muted">Add Members to your meeting</h4><br>
                    </div>
                    <!--/ Welcome Text-->

                    <!--Search-->
                    <div class="input-group">
                        <div class="form-outline">
                            <input type="search" id="searchBar" placeholder="Member name" class="form-control" />
                            <label class="form-label" for="searchBar">Search</label>
                        </div>
                        <button type="button" class="btn btn-primary" onclick="search()">
                            <img src="/meetingroommanagement/resources/images/icon_search.png" alt="search" style="width: 15px;">
                        </button>
                    </div>
                    <!--Search-->

                    <!--Search Result-->
                    <div class="col-md-6" id="searchResults"></div>
                    <!--Search Result-->

                </div>
                <!--Added members-->
                <div class="col-10 col-sm-8 col-md-9 col-lg-6 col-xl-5">
                    <div class="d-flex flex-row align-items-center justify-content-center justify-content-lg-start my-4 mt-5">
                        <h4 class="text-muted">Added Members</h4><br>
                    </div>
                    <!--Added Members-->
                    <div class="col-md-8" id="membersAdded"></div>
                    <!--Added Members-->
                    <button onclick="submitToBack()" class="btn btn btn-primary">Create Meeting</button>
                </div>
                <!--Added members-->
            </div>
        </div>
    </main>

    <!--Footer-->
    <footer id="footer">
        <div>
            <hr class="my-2">
        </div>
        <div class="footer-copyright d-flex align-items-center justify-content-center">
            � 2021 Copyright: HSCC Meettings
        </div>
    </footer>
    <!--/.Footer-->

    <!--scripts-->
    <script src="/meetingroommanagement/scripts/scripts.js"></script>
    <script>
        let capacity = ${capacity};
        let searchedMembers = [];
        let addedMembers = [];

        function search() {
            // temp JSON array
            let resultSet = [{
                "userId": 1,
                "name": "Amit",
                "email": "amit.kumar@hscc.co.in"
            }, {
                "userId": 2,
                "name": "Raj",
                "email": "raj.kumar@hscc.co.in"
            }, {
                "userId": 3,
                "name": "Sachin",
                "email": "sachin.kumar@hscc.co.in"
            }, {
                "userId": 4,
                "name": "Rahul",
                "email": "rahul.kumar@hscc.co.in"
            }];
            // temp JSON array

            let resultsDiv = document.getElementById('searchResults');
            resultsDiv.innerHTML = `<h6>searching...</h6>`;

            let searchString = document.getElementById('searchBar').value;

            let xhttp = new XMLHttpRequest();
            let method = "GET";
            let url = "http://localhost:8080/meetingroommanagement/meeting?search=" + searchString;
            xhttp.open(method, url, true);
            xhttp.send();
			xhttp.onload = function(e) {
            	let resultSet = JSON.parse(xhttp.responseText);
                if (resultSet.length > 0) {
                    searchedMembers = resultSet;
                    let htmlString = "";
                    searchedMembers.forEach(member => {
                        htmlString += `<span style="text-decoration: none; cursor: pointer;">
                                        <div id=`+member.userId+` onclick="validateSelectedUser(event, this.id)" class="user-card-body px-4 p-0">
                                            <h6 class="user-name-searched text-bold pt-1">`+member.name+`</h6>
                                            <p class="user-name-searched pb-1">`+member.email+`</p>
                                        </div>
                                    </span>`;
                    });
                    resultsDiv.innerHTML = htmlString;
                } else {
                    resultsDiv.innerHTML = `<h6>0 results</h6>`;
                }
            }
            
            if (resultSet.length > 0) {
                searchedMembers = resultSet;
                let htmlString = "";
                resultSet.forEach(member => {
                    htmlString += `<span style="text-decoration: none; cursor: pointer;">
                                        <div id=`+member.userId+` onclick="validateSelectedUser(event, this.id)" class="user-card-body px-4 p-0">
                                            <h6 class="user-name-searched text-bold pt-1">`+member.name+`</h6>
                                            <p class="user-name-searched pb-1">`+member.email+`</p>
                                        </div>
                                    </span>`;
                });
                resultsDiv.innerHTML = htmlString;
            } else {
                resultsDiv.innerHTML = `<h6>0 results</h6>`;
            }
            
            xhttp.onerror = function(e){
            	resultsDiv.innerHTML = `<h6>no Results</h6>`;
            }
        }

        function validateSelectedUser(event, id) {
            event.preventDefault();
            let resultsDiv = document.getElementById('searchResults');

            if (capacity > addedMembers.length) {
                let canAdd = addedMembers.find(member => member.userId == id);
                if (canAdd == undefined) {
                    let addThisMember = searchedMembers.find(member => member.userId == id);
                    console.log(addThisMember);
                    addedMembers.push(addThisMember);
                    addToMeeting(addThisMember);
                } else {
                    resultsDiv.innerHTML = `<h6 style="color:red;">Member already added</h6>`;
                }
            } else {
                resultsDiv.innerHTML = `<h6 style="color:red;">Meeting room capacity is full</h6>`;
            }
        }

        function addToMeeting(member) {
            let addedMemberHtml = "";
            addedMemberHtml = `<div class="user-card-body px-4 py-2">
                            <h6 class="user-name-searched text-bold pt-1">`+member.name+`</h6>
                            <p class="user-name-searched pb-1">`+member.email+`</p>
                        </div>
                        <hr>`;
            document.getElementById("membersAdded").insertAdjacentHTML('beforeend', addedMemberHtml);
        }
        
        function submitToBack(){
        	let xmlhttp = new XMLHttpRequest();   // new HttpRequest instance 
        	let theUrl = "http://localhost:8080/meetingroommanagement/meeting/submit";
        	xmlhttp.open("POST", theUrl , true);
        	xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        	xmlhttp.send(JSON.stringify(addedMembers));
        	xmlhttp.onload = function(e) {
        		window.location.replace(xmlhttp.responseURL);
        	}
        }
    </script>
    <!--scripts-->
</body>

</html>