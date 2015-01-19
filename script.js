var button = document.getElementById("submitbtn");
var url = document.getElementById("inputURL");
console.log(button);
button.onclick = receiveSubtitleList;
function receiveSubtitleList(url) {

	console.log("Running AJAX");
	var request = new XMLHttpRequest();
	request.open('POST', 'list.php', true);
	request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
	request.send("url="+url);
	request.onreadystatechange = function() {
		 if (request.readyState==4 && request.status==200) {
		 	document.getElementById("subtitleList").innerHTML = request.responseText;
		 	console.log(request);
		 }
	}
	//prevent the the form from submitting 
	return false;
}