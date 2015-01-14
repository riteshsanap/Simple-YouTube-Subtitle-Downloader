<?php 
include_once("youtube.php");
// $param = array('signature', 'expire', 'v', 'key', 'caps', 'ars_langs', 'sparams', 'tlang', 'asrs', 'lang', 'kind', 'name');
// $params["signature"] = $_GET["signature"];
// $params["expire"] = $_GET["expire"];
// $params["v"] = $_GET["v"];
// $params["key"] = $_GET["key"];
// $params["caps"] = $_GET["caps"];
// $params["ars_langs"] = $_GET["asr_langs"];
// $params["asrs"] = $_GET["asrs"];
// foreach ($param as $key => $value) {
// 	if(isset($_GET[$value])) {
// 		$params[$value] = $_GET[$value];
// 	}
// }
// $params['fmt'] = 'srt';
// print_r($params);
$source = YouTube::getSource("https://www.youtube.com/watch?v=".$_GET["v"]);
$url = YouTube::getMagicURL($source);
$title = YouTube::getTitle($source);
$params = YouTube::getParams($url);
$param = array('fmt', 'lang', 'tlang', 'kind');
foreach ($param as $key => $value) {
	if(isset($_GET[$value])) {
		$params[$value] = $_GET[$value];
	}
	if(!isset($params["fmt"])) {
		$params["fmt"] = "srt";
	}
}

// $params['fmt'] = 'srt';
// $params['lang'] = $_GET["lang"];
// $params['tlang'] = $_GET["tlang"];
// $params['kind'] = $_GET["kind"];

$output = YouTube::getSubtitleURL($params);

if(!isset($title)) {
	$filename = $params["v"];
	$filename .= "_";
	if(isset($params["tlang"])) {
		$filename .= $params["tlang"];
	} else {
		$filename .= $params["lang"];
	}
} else {
	$filename = $title;
}

header("Content-type: text/plain");
header("Content-Disposition: attachment; filename=".$filename.".srt");
 
print $output;
// print_r($output);
?>