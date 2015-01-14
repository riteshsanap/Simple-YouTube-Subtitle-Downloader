<!DOCTYPE html>
<html>
<head>
	<title>YouTube Substitle Downloader</title>
	<style type="text/css">
/*! normalize.css v3.0.2 | MIT License | git.io/normalize */html{font-family:sans-serif;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%}body{margin:0}article,aside,details,figcaption,figure,footer,header,hgroup,main,menu,nav,section,summary{display:block}audio,canvas,progress,video{display:inline-block;vertical-align:baseline}audio:not([controls]){display:none;height:0}[hidden],template{display:none}a{background-color:transparent}a:active,a:hover{outline:0}abbr[title]{border-bottom:1px dotted}b,strong{font-weight:700}dfn{font-style:italic}h1{font-size:2em;margin:.67em 0}mark{background:#ff0;color:#000}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sup{top:-.5em}sub{bottom:-.25em}img{border:0}svg:not(:root){overflow:hidden}figure{margin:1em 40px}hr{-moz-box-sizing:content-box;box-sizing:content-box;height:0}pre{overflow:auto}code,kbd,pre,samp{font-family:monospace,monospace;font-size:1em}button,input,optgroup,select,textarea{color:inherit;font:inherit;margin:0}button{overflow:visible}button,select{text-transform:none}button,html input[type=button],input[type=reset],input[type=submit]{-webkit-appearance:button;cursor:pointer}button[disabled],html input[disabled]{cursor:default}button::-moz-focus-inner,input::-moz-focus-inner{border:0;padding:0}input{line-height:normal}input[type=checkbox],input[type=radio]{box-sizing:border-box;padding:0}input[type=number]::-webkit-inner-spin-button,input[type=number]::-webkit-outer-spin-button{height:auto}input[type=search]{-webkit-appearance:textfield;-moz-box-sizing:content-box;-webkit-box-sizing:content-box;box-sizing:content-box}input[type=search]::-webkit-search-cancel-button,input[type=search]::-webkit-search-decoration{-webkit-appearance:none}fieldset{border:1px solid silver;margin:0 2px;padding:.35em .625em .75em}legend{border:0;padding:0}textarea{overflow:auto}optgroup{font-weight:700}table{border-collapse:collapse;border-spacing:0}td,th{padding:0}
body {background: #f1f1f1;}
#container {width: 600px;margin:20px auto;background: #fff;border:1px solid #eee; padding: 20px;}
ul {margin: 0; padding: 0;}
li {list-style: none;}
ul#sublist a {display: block; background: #000; color: #fff; border-bottom: 1px solid #ddd; padding: 20px;}
#footer {position: fixed; bottom: 0; left: 50%;}
#footer a {color: #333; font-family: monospace;}
form input {padding: 10px 5px; border: 1px solid #ddd;}
form input[type="submit"] {background: #333; border:1px solid #ddd; color: #fff; padding: 10px;}
</style>
</head>
<body>
<div id="container">
	<header><h1>YouTube Subtitle Downloader</h1></header>
	<form method="post" action="list.php">
		<input type="text" class="url" placeholder="YouTube video URL" name="url">
		<input type="submit" class="submit">
	</form>