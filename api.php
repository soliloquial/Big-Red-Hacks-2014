<?php
/*
$stream = http_get_request_body_stream();
$contents = stream_get_contents($stream);
$filename = rand(1,1000000000) . ".bmp";
file_put_contents($filename, $contents);*/
$lang = $_GET['lang'];
$url = $_GET['url'];

$ch = curl_init("https://www.google.com/searchbyimage?image_url=".$url);
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
curl_setopt($ch, CURLOPT_HEADER, false);
curl_setopt($ch, CURLOPT_HTTPHEADER, array('User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11')); 
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$out = curl_exec($ch);
curl_close($ch);

preg_match('#style="font-style:italic">(.*?)</a>#', $out, $matches);
$name = $matches[1];

//$url = 'http://api.rhine.io/sdf0b913e4b07b5243b7f527/closest_entities/'.urlencode($name);
$url = 'http://en.wikipedia.org/wiki/'.rawurlencode($name);
$data = file_get_contents($url);
$data = preg_replace('#<table class="infobox(.*?)</table>#s','',$data, -1);
$data = preg_replace('#class="hatnote">(.*?)</div>#s', '', $data);
//print $count .$data;
preg_match_all('#title="(.*?)">(?<entity>.*?)</a>#', $data, $matches);
$entities = $matches['entity'];
$entities = preg_grep('#^[a-z ]+$#', $entities);
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

$newArr = array_map(function($val) use($token) {
	$newval = [];
	$json = file_get_contents("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=".rawurlencode($val));
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