<?php 
include_once("youtube.php");

$source = YouTube::getSource("https://www.youtube.com/watch?v=".$_GET["v"]);
$url = YouTube::getMagicURL($source);
$title = YouTube::getTitle($source);
$params = YouTube::getParams($url);
$param = array('fmt', 'lang', 'tlang', 'kind', 'name');
foreach ($param as $key => $value) {
	if(isset($_GET[$value])) {
		$params[$value] = $_GET[$value];
	}
	if(!isset($params["fmt"])) {
		$params["fmt"] = "srt";
	}
}

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
$filename .= ".srt";
header("Content-type: text/plain");

/**
 * Added Quotes around file name so that it does not break
 * during spaces and special characters
 */
header("Content-Disposition: attachment; filename='$filename'");
 
print $output;

?>