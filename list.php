<?php 
/**
 * Display List of Subtitles
 */
include_once ("youtube.php");
if($_POST["url"]) {
	$url = $_POST["url"];	
} else {
	$url = NULL;
}
if ($url) {
	$source = YouTube::getSource($url);
	$vID = YouTube::getID($url);
	$murl = YouTube::getMagicURL($source);
	$params = YouTube::getParams($murl);
	$list = YouTube::getSubList($params);
	$list = simplexml_load_string($list);
	$query = YouTube::queryParam($params);
}
 ?>
 <?php include("header.php"); ?>
	<?php  if($url && $murl && $list) { ?>
		<header><h2>List of Subtitles</h2></header>
		<?php $defaultlang = YouTube::getDefaultLang($list); ?>
  		<h2>Available</h2>
  		<ul id="sublist">
  		  	<?php foreach ($list->track as $key => $value) {
  			$output = "<li><a href='download.php?lang=".$value["lang_code"];
  			$output .= '&v='.$vID;
  			if ($value["kind"]) {
  				$output .= "&kind=".$value["kind"];
  			}
  			if($value["name"]) {
  				$output .= '&name='.$value["name"];
  			}
  			$output .= "'>". $value["lang_original"];
  			if ($value["kind"]) {
  				$output .= " (".$value['kind'].")";
  			}
  			if($value["name"] !='') {
  				$output .= " (".$value["name"].")";
  			}
  			$output .= "</a></li>";
  			echo $output;
  		} ?>
  		</ul>
		<!-- <hr> -->
		<h6>Translated Above Subtitles to :</h6>
		<ul id="sublist">
  		<?php foreach ($list->target as $key => $value) {
  			$output = "<li><a href='download.php?tlang=".$value["lang_code"];
  			$output .= '&v='.$vID;
  			$output .= '&lang='.$defaultlang;
  			if ($value["kind"]) {
  				$output .= "&kind=".$value["kind"];
  			}
  			$output .= "'>". $value["lang_original"];
  			if ($value["kind"]) {
  				$output .= " (".$value['kind'].")";
  			}
  			$output .= "</a></li>";
  			echo $output;
  		} ?>
  		</ul>
  	<?php } else {
      echo "<header><h2>No Subtitles found for the video</h2></header>";
      } ?>
<?php include("footer.php"); ?>