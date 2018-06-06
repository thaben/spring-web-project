<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<c:import url="header.jsp"></c:import>  
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%> 


<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>


<%@ page import="java.util.*"%>






<div class="body-section col-md-10 jumbotron" style="width: 100%;position: inherit;">
	<div class="col-md-3">
		<h4>Conversation Details</h4><br>
		<h5>Professional:${conversation.interlocutor.firstName} ${conversation.interlocutor.lastName} </h5>
		<h5>Questioner:${conversation.creator.firstName} ${conversation.creator.lastName}</h5>
		<h5>Total Messages: ${totalMessages}</h5>
	</div>
	<div class="col-md-8">
		<div class="col-md-10 right-section" style="overflow: auto">
			<div class="message">
				<ul>
				
				<!-- dummy text -->
					<li class="msg-left">
						<div class="msg-left-sub">
							<img class="img-circle" src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSEPQDZ4QSXjrXEAaHaRDTjX_4_Lfha2QiYM30-mD9gUTErZL4f">
							<div class="msg-desc">Lorem ipsum dolor sit amet,
								consectetur adipisicing elit, sed do eiusmod tempor incididunt
								ut labore et dolore magna aliqua.</div>
							<small>05:25 am</small>
						</div>
					</li>
				




					<c:forEach items="${list}" var="message">



						<c:if test="${message.sender.username == sessionScope.username}">
							<li class="msg-right">
								<div class="msg-left-sub">
									<img class="img-circle" src="${pageContext.request.contextPath}/profile_images/${sessionScope.id}.jpg" onError="this.onerror=null;this.src='${pageContext.request.contextPath}/images/defaultprofile.png';">
									<div class="msg-desc">${message.text}</div>
									<small><fmt:formatDate pattern="dd-MM-yyyy hh:mm:ss a" value="${message.dateTime}" /></small>
								</div>
							</li>

						</c:if>
						<c:if test="${message.sender.username != sessionScope.username}">

							<li class="msg-left">
								<div class="msg-left-sub">
									<img class="img-circle" src="${pageContext.request.contextPath}/profile_images/${sessionScope.id}.jpg" onError="this.onerror=null;this.src='${pageContext.request.contextPath}/images/defaultprofile.png';">
									<div class="msg-desc">${message.text}</div>
									<small>${message.dateTime}</small>
								</div>
							</li>

						</c:if>
					</c:forEach>





				</ul>
			</div>

		</div>
		<div class="right-section-bottom">



			<form action="/sendMessage" method="POST" accept-charset="ISO-8859-1">

				<input type="text" name="text" placeholder="type here..."> <input
					type="hidden" name="conversationId" value="${conversationId}">
				<button class="btn-success" style="font-size:20px;width:30px;padding:5px;">
					<i class="fa fa-level-up"></i>
				</button>
				
			</form>
		</div>
	</div>
</div>






<c:import url="footer.jsp"></c:import>  