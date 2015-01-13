<?php 
Class YouTube {
	/**
	 * Java Module
	 */
	// public static String getMagicURL(String YouTubeWebSource) throws UnsupportedEncodingException {
 //        String result, s;
 //        String[] strings;
        
 //        // "ttsurl": "http:\/\/www.youtube.com\/api\/timedtext?key=yttt1\u0026hl=en_US\u0026expire=1372005633\u0026sparams=asr_langs%2Ccaps%2Cv%2Cexpire\u0026signature=4FE7A8E1FAAE338EAB840B58F3E05D1963F5550D.CE40D0149565FE4035DD4E914C144E864CE28302\u0026caps=asr\u0026v=L1hIAF5YvN0\u0026asr_langs=ko%2Cde%2Cja%2Cpt%2Cen%2Cit%2Cnl%2Ces%2Cru%2Cfr",
 //        strings = YouTubeWebSource.split("ttsurl");
 //        s = strings[1];
 //        strings = s.split(",");
 //        s = strings[0];
 //        strings = s.split("\"");
 //        s = strings[2];
        
 //        s = s.replace("\\/", "/");
 //        s = s.replace("\\u0026", "&");
        
 //        result = URLDecoder.decode(s, "UTF-8");
        
 //        return result;
 //    }
	public static function getSource($url='') {
		$ch = curl_init();  
	 
	    curl_setopt($ch,CURLOPT_URL,$url);
	    curl_setopt($ch,CURLOPT_RETURNTRANSFER,true);
	//	curl_setopt($ch,CURLOPT_HEADER, false); 
	 
	    $output=curl_exec($ch);
	 
	    curl_close($ch);
	    return $output;
	}

	public static function getMagicURL($source='') {
		
	}
}
function httpGet($url)
{
    $ch = curl_init();  
 
    curl_setopt($ch,CURLOPT_URL,$url);
    curl_setopt($ch,CURLOPT_RETURNTRANSFER,true);
//	curl_setopt($ch,CURLOPT_HEADER, false); 
 
    $output=curl_exec($ch);
 
    curl_close($ch);
    return $output;
}
 //$url ="https://www.youtube.com/api/timedtext?key=yttt1&caps=asr&asr_langs=it%2Ces%2Cnl%2Cfr%2Cde%2Cru%2Cja%2Cko%2Cen%2Cpt&expire=1421086605&v=Fzzj5MshwCk&signature=DB194DF07E1D9BB7F5D39C76BC3079E88B316B13.C4C24634345C1CA40EBCA0FE0A1C8D81A288A580&sparams=asr_langs%2Ccaps%2Cv%2Cexpire&hl=en-GB&type=track&lang=it&name&kind=asr&fmt=1";
// $output ="https://www.youtube.com/api/timedtext";
$output = "http://video.google.com/timedtext";
$output .="?key=yttt1"; // required
$output .="&caps=asr"; // required 
$output .="&asr_langs=it%2Ces%2Cnl%2Cfr%2Cde%2Cru%2Cja%2Cko%2Cen%2Cpt"; // required
// it,es,nl,fr,de,ru,ja,ko,en,pt
// 
$output .="&expire=1421086605"; //required
// $output .="&v=Fzzj5MshwCk"; // required
$output .="&v=23H8IdaS3tk"; // required
$output .="&signature=DB194DF07E1D9BB7F5D39C76BC3079E88B316B13.C4C24634345C1CA40EBCA0FE0A1C8D81A288A580";
$output .="&sparams=asr_langs%2Ccaps%2Cv%2Cexpire"; // required
// asr_langs,caps,v,expire
// 
// $output .="&hl=en-GB";
$output .="&type=track"; // required
$output .="&lang=en"; //required
// $output .="&name";
$output .="&kind=asr"; // required
// $output .="&fmt=1";
$output .="&fmt=srt"; // format set to SRT instead of default timedtext
// $url="https://www.youtube.com/api/timedtext";
// $url .= ""
// echo httpGet($output);

?>