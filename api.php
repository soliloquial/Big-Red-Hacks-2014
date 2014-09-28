<?php
/*
$stream = http_get_request_body_stream();
$contents = stream_get_contents($stream);
$filename = rand(1,1000000000) . ".bmp";
file_put_contents($filename, $contents);*/
$lang = $_GET['lang'];
$url = $_GET['url'];
$h = fopen("log.txt", "a");
fwrite($h, $_SERVER['QUERY_STRING']."\r\n");
fclose($h);

$ch = curl_init("https://www.google.com/searchbyimage?image_url=".$url);
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
curl_setopt($ch, CURLOPT_HEADER, false);
curl_setopt($ch, CURLOPT_HTTPHEADER, array('User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11')); 
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$out = curl_exec($ch);
curl_close($ch);

preg_match('#style="font-style:italic">(.*?)</a>#', $out, $matches);
$name = $matches[1];

if(!isset($_GET['rhine'])) {
	$url = 'http://www.wikipedia.org/search-redirect.php?family=wikipedia&search='.rawurlencode($name).'&language=en&go=++%E2%86%92++&go=Go';
	$data = file_get_contents($url);
	preg_match('#<div class=\'mw-search-result-heading\'><a href="(?<path>.*?)"#', $data, $matches);
	if(isset($matches['path'])) {
		$url = 'http://en.wikipedia.org'.$matches['path'];
		$data = file_get_contents($url);
	}
	$data = preg_replace('#<table(.*?)</table>#s','',$data, -1);
	$data = preg_replace('#class="hatnote">(.*?)</div>#s', '', $data);
	//print $count .$data;
	preg_match_all('#title="(.*?)">(?<entity>.*?)</a>#', $data, $matches);
	$entities = $matches['entity'];
	$entities = preg_grep('#^[a-z ]+$#', $entities);
} else {
	$url = 'http://54.91.103.146/sdf0b913e4b07b5243b7f527/closest_entities/'.rawurlencode($name);
	$ch = curl_init($url);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	$out = curl_exec($ch);
	$entities = json_decode($out)->closest_entities;
	$entities = str_replace('_', ' ', $entities);
}
$entities = array_slice($entities, 0,10);

//Get token for Bing
$postData = "grant_type=client_credentials&client_id=hacklang&client_secret=ZDM29Y93FKQVKXe9L3ZIPu3gcvhvptu9Ps%2BkLofIoWQ%3D&scope=http://api.microsofttranslator.com";
$postUrl = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";
$ch = curl_init($postUrl);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $postData);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$out = curl_exec($ch);
$data = json_decode($out);
$token = $data->access_token;

$newArr = array_map(function($val) use($token, $lang) {
	$newval = [];
	$json = file_get_contents("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=".rawurlencode($val)."&imgsz=small");
	$obj = json_decode($json);
	$newVal['name'] = $val;
	$newVal['imageUrl'] = $obj->responseData->results[0]->unescapedUrl;

	$url = "http://api.microsofttranslator.com/v2/Http.svc/Translate?text=".rawurlencode($val)."&from=en&to=".$lang;

	$ch = curl_init($url);
	curl_setopt($ch, CURLOPT_HEADER, false);
	curl_setopt($ch, CURLOPT_HTTPHEADER, array('Authorization: Bearer '.$token)); 
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	$out = curl_exec($ch);
	$root = simplexml_load_string($out);
	$newVal['translated'] = $root->__toString();
	return $newVal;

}, $entities);

header('Content-type','application/json');
echo json_encode($newArr);
?>