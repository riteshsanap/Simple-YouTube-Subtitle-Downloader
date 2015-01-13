<?php 
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
		/**
		 * Get ttsurl from YouTube page source Code
		 */
		preg_match_all('/"ttsurl":.".*?"/', $source, $matches);

		/**
		 * remove ttsurl and Double Quotes
		 */
		$value = preg_replace('/"ttsurl".*?"/', NULL, $matches[0]);

		/**
		 * Remove Double Quote that is at the End of the Line
		 */
		$value = preg_replace('/"/', NULL, $value[0]);

		/**
		 * Replace \u0026 to & 
		 */
		$value = str_replace("\u0026", "&", $value);

		/**
		 * Remove Escaped "/" from the starting https protocol
		 */
		$value = str_replace("\\/", "/", $value);

		/**
		 * replace Escaped ","
		 */
		$value = str_replace("%2C", ",", $value);
		
		return $value;
	}

	public static function getParams($url) {
		/**
		 * Split the URL in Two parts 
		 */
		$value = explode("?", $url);

		/**
		 * Split all Params into an Array
		 */
		$params = explode("&", $value[1]);

		/**
		 * Split keys and values and store them in an array
		 */
		foreach ($params as $value) {
			$output= explode("=", $value);
			$param[$output[0]] = $output[1];
		}

		return $param;

	}

	public static function getSubList($params) {
		$url = 'https://www.youtube.com/api/timedtext?type=list';
		foreach ($params as $key => $value) {
			$url .='&'.$key.'='.urlencode($value);
		}
		$url .="&asrs=1&tlangs=1";
		return httpGet($url);
	}

	public static function getSubtitle($params) {
		$url = 'https://www.youtube.com/api/timedtext?type=track';
		foreach ($params as $key => $value) {
			$url .='&'.$key.'='.urlencode($value);
		}

		return httpGet($url);
	}
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
$source = YouTube::getSource("https://www.youtube.com/watch?v=23H8IdaS3tk");
// $source = '<script>var ytplayer = ytplayer || {};ytplayer.config = {"messages": {"player_fallback": ["Adobe Flash Player or an HTML5-supported browser is required for video playback. \u003ca href=\"http:\/\/get.adobe.com\/flashplayer\/\"\u003eGet the latest Flash Player \u003c\/a\u003e \u003ca href=\"\/html5\"\u003eLearn more about upgrading to an HTML5 browser\u003c\/a\u003e"]}, "args": {"eventid": "0sK0VJS3OqqBoQP6sIHwAg", "no_get_video_log": "1", "enablecsi": "1", "ucid": "UCx2ABiH5rTZdKryN21MVR7w", "focEnabled": "1", "iurlmq": "https:\/\/i.ytimg.com\/vi\/Fzzj5MshwCk\/mqdefault.jpg", "tmi": "1", "host_language": "en-GB", "enablejsapi": 1, "advideo": "1", "watch_ajax_token": "QUFFLUhqbllqeGZmZUxPNElwTkxfZlpmczZZQVVfYmhIUXxBQ3Jtc0tsakFvN0RmSDZnNGFKSEJxYWtnMUFoMVNLd0Fya2VpYVEyTVVSSTRsOFFRczk0bmxUNDNvVGdsTmpORTFkQnFtRnBFaWw3b0JWbjBhY2pPa1JFeW5Eemg1YUw3VmJVaXRnLUt3d0tjUEJzS2d2UzNjMA==", "cosver": "10_9_5", "iurlmaxres": "https:\/\/i.ytimg.com\/vi\/Fzzj5MshwCk\/maxresdefault.jpg", "cc_fonts_url": "https:\/\/s.ytimg.com\/yts\/swfbin\/player-vfl4nHN8A\/fonts708.swf", "cc3_module": "1", "storyboard_spec": "https:\/\/i.ytimg.com\/sb\/Fzzj5MshwCk\/storyboard3_L$L\/$N.jpg|48#27#100#10#10#0#default#tn4VshA9Z1bSVYbPEvmz3J1XREE|80#45#106#10#10#1000#M$M#LXuIZbxX22mJIkZq8lvfF-VYEIQ|160#90#106#5#5#1000#M$M#FlOB1uYCC-XGIN8hjq3fWCnaE6I", "ssl": "1", "author": "Honda India", "allow_ratings": "1", "dash": "1", "iv_load_policy": "1", "timestamp": "1421132499", "cc_font": "Arial Unicode MS, arial, verdana, _sans", "thumbnail_url": "https:\/\/i.ytimg.com\/vi\/Fzzj5MshwCk\/default.jpg", "watermark": ",https:\/\/s.ytimg.com\/yts\/img\/watermark\/youtube_watermark-vflHX6b6E.png,https:\/\/s.ytimg.com\/yts\/img\/watermark\/youtube_hd_watermark-vflAzLcD6.png", "hl": "en_GB", "aid": "P8DGENxkQ7g", "cbr": "Chrome", "user_display_name": "Ritesh Sanap", "cc_load_policy": "2", "dashmpd": "https:\/\/manifest.googlevideo.com\/api\/manifest\/dash\/requiressl\/yes\/sparams\/as%2Cid%2Cip%2Cipbits%2Citag%2Cmm%2Cms%2Cmv%2Cplayback_host%2Crequiressl%2Csource%2Cexpire\/id\/o-AL3wb5g4V4iAj_-K71pLG52YTf2gvCGE4-3nvs7gg3Kf\/signature\/E5A5D4CA31E929D940CDC3C5D49D65AA69DC68C1.1F58EBFBC45E461F29D1AB0787377D4478EBD4B7\/source\/youtube\/fexp\/3300105%2C3300105%2C3300133%2C3300133%2C3300137%2C3300137%2C3300161%2C3300161%2C3310703%2C3310703%2C3311907%2C3311907%2C900718%2C916641%2C927622%2C932404%2C9405793%2C941004%2C943917%2C945093%2C947209%2C947218%2C947225%2C948124%2C952302%2C952605%2C952901%2C955301%2C957103%2C957105%2C957201%2C959701%2C962712\/ms\/au\/mv\/m\/as\/fmp4_audio_clear%2Cwebm_audio_clear%2Cfmp4_sd_hd_clear%2Cwebm_sd_hd_clear%2Cwebm2_sd_hd_clear\/mt\/1421132425\/itag\/0\/key\/yt5\/ip\/183.87.8.58\/mm\/31\/playback_host\/r3---sn-4p8xoxu-cvhl.googlevideo.com\/upn\/91QZiTINN0s\/expire\/1421154099\/ipbits\/0\/sver\/3", "user_display_image": "https:\/\/yt3.ggpht.com\/-Q9GorTWpK6g\/AAAAAAAAAAI\/AAAAAAAAAAA\/QCcyHpJYQxo\/s28-c-k-no\/photo.jpg", "fexp": "3300105,3300105,3300133,3300133,3300137,3300137,3300161,3300161,3310703,3310703,3311907,3311907,900718,916641,927622,932404,9405793,941004,943917,945093,947209,947218,947225,948124,952302,952605,952901,955301,957103,957105,957201,959701,962712", "view_count": "1251510", "plid": "AAUMgzHPoGRDHWn7", "pltype": "contentugc", "of": "Btkt7_WLakozE4MYg-7OfQ", "keywords": "Kapil Sharma (TV Personality),Honda Cars India (Business Operation)", "baseUrl": "https:\/\/googleads.g.doubleclick.net\/pagead\/viewthroughconversion\/962985656\/", "c": "WEB", "vid": "Fzzj5MshwCk", "iurlsd": "https:\/\/i.ytimg.com\/vi\/Fzzj5MshwCk\/sddefault.jpg", "cr": "IN", "ttsurl": "https:\/\/www.youtube.com\/api\/timedtext?caps=asr\u0026sparams=asr_langs%2Ccaps%2Cv%2Cexpire\u0026signature=DF91F8F9F50BFEC18B30A821E22E3D0573771A0D.CA1C666D554A4AD33EA0ABDEC448727AB1E7C23D\u0026v=Fzzj5MshwCk\u0026hl=en_GB\u0026asr_langs=es%2Cde%2Cru%2Cit%2Cko%2Cfr%2Cen%2Cpt%2Cja%2Cnl\u0026key=yttt1\u0026expire=1421157699", "allow_embed": "1", "vq": "auto", "ptk": "youtube_none", "user_age": "20", "vm": "CAI", "cl": "83543002';
// echo $source;
// var_dump(explode('ttsurl',$source));
// $pattern = ;

// var_dump(preg_match_all('/(\s+?)"\w+":.+(".+",?)/mi', $source, $matches));

// var_dump(preg_match_all('ttsurl', $source, $matches));

// echo $value[];
// var_dump($matches[1]);
// var_dump(explode("&",YouTube::getMagicURL($source)));
// $value = explode("?",YouTube::getMagicURL($source));
// var_dump($value[1]);
// $value = explode("&", $value[1]);
$url = YouTube::getMagicURL($source);

$params = YouTube::getParams($url);
var_dump($params);
$list = YouTube::getSubList($params);
	
// var_dump(simplexml_load_string($list));

$params['lang'] = "en";
$params['fmt'] = "srt";
$subtitle = YouTube::getSubtitle($params);
var_dump($subtitle);
?>