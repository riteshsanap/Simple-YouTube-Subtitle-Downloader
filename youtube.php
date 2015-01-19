<?php 
Class YouTube {
	public static function getSource($url='') {
		$ch = curl_init();  
	 
	    curl_setopt($ch,CURLOPT_URL,$url);
	    curl_setopt($ch,CURLOPT_RETURNTRANSFER,true);
	//	curl_setopt($ch,CURLOPT_HEADER, false); 
	 
	    $output=curl_exec($ch);
	 
	    curl_close($ch);
	    return $output;
	}

	public static function getID($url) {
		/**
		 * Taken from 
		 * http://stackoverflow.com/questions/6556559/youtube-api-extract-video-id
		 */
		$pattern = 
	        '%^# Match any youtube URL
	        (?:https?://)?  # Optional scheme. Either http or https
	        (?:www\.)?      # Optional www subdomain
	        (?:             # Group host alternatives
	          youtu\.be/    # Either youtu.be,
	        | youtube\.com  # or youtube.com
	          (?:           # Group path alternatives
	            /embed/     # Either /embed/
	          | /v/         # or /v/
	          | /watch\?v=  # or /watch\?v=
	          )             # End path alternatives.
	        )               # End host alternatives.
	        ([\w-]{10,12})  # Allow 10-12 for 11 char youtube id.
	        $%x'
	        ;
	    $result = preg_match($pattern, $url, $matches);
	    if (false !== $result) {
	        return $matches[1];
	    }
	    return false;

	}

	public static function getTitle($source) {
		/**
		 * RegEx to get Video Title from YouTube Page
		 */
		preg_match_all('/<title>(.*?)<\/title>/', $source, $matches);

		/**
		 * Remove <title> tag
		 */
		$title = str_replace("<title>", NULL, $matches[0]);

		/**
		 * Remove Closing </title> tag
		 */
		$title = str_replace("</title>", NULL, $title[0]);
		
		return $title;
	}

	public static function queryParam($params='') {
		/**
		 * Concatenate all the params together and Encode the value with URLencode
		 */
		$url =''; // Added because PHP giving silent error of undefined variable
		foreach ($params as $key => $value) {
			$url .='&'.$key.'='.urlencode($value);
		}
		return $url;
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
		/**
		 * URL to retrieve List of available Subtitles for the Video
		 */
		$url = 'https://www.youtube.com/api/timedtext?type=list';

		/**
		 * Add Parameters to the URL
		 * required params are v, signature, expire, caps, asr_langs, sparams, key, asrs
		 */
		$url .= self::queryParam($params);

		/**
		 * add Extra parameters which will bring the full list
		 */
		$url .="&asrs=1&tlangs=1";

		return self::getSource($url);
	}

	public static function getSubtitleURL($params) {
		/**
		 * Almost same as the above function
		 * need to pass few extra params such as lang, fmt and tlang
		 * above parameters depends on the users request.
		 */
		$url = 'https://www.youtube.com/api/timedtext?type=track';
		
		$url .= self::queryParam($params);

		return self::getSource($url);
	}

	public static function getAvailableSubs($list) {
		return $list->track;
	}

	public static function getTransSubs($list) {
		return $list->traget;
	}

	public static function getDefaultLang($list) {
		/**
		 * Get Default Language from subtitle List
		 * Used to translate other Subs
		 */
		foreach ($list->track as $key => $value) {
			if ($value["lang_default"]) {
  				$defaultlang = $value["lang_code"];
  			} elseif ($value["cantran"]) {
  				$defaultlang = $value["lang_code"];
  			}

  			if($value["kind"]) {
  				$defaultlang .= "&kind=".$value["kind"];
  			}
  			if($value["name"]) {
  				$defaultlang .= "&name=".$value["name"];
  			}

		}

		return $defaultlang;
	}

}
?>