<!DOCTYPE html>
<html lang="en">
<head>
  <title><? echo $TITLE ?></title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="description" content="<? echo $DESCRIPTION ?>" />
  <meta name="abstract" content="<? echo $DESCRIPTION ?>" />
   <link href="style.css" rel="stylesheet" media="screen" />
<!--  <link href="img/favicon.ico" rel="shortcut icon" type="image/x-ico; charset=binary" /> -->
  <script src="http://code.jquery.com/jquery-latest.js"></script>
</head>
<body>

  <!-- Header -->

  <div id="nmenu">
  <nav>
  	<ul>
		<li><a href="index.php">Home</a></li>
		<li class="dropdown <? ($SECTION==LANG_SECTION ? 'active' : '') ?>">
        <a href="#">Language Identification</a>
			<ul class="dropdown-menu">
				<li><a href="lang.php">Description</a></li>
				<li><a href="lang.php#seccion2">How to use it?</a></li>
				<li><a href="lang.php#seccion3">Output</a></li>
				<li><a href="lang.php#seccion4">Troubleshooting</a></li>
			</ul>
		</li>
		<li class="dropdown <? ($SECTION==CLASS_SECTION ? 'active' : '') ?>">
        <a href="#">Text Classification</a>
			<ul class="dropdown-menu">
				<li><a href="class.php">Description</a></li>
				<li><a href="class.php#seccion2">How to use it?</a></li>
				<li><a href="class.php#seccion3">Output</a></li>
				<li><a href="class.php#seccion4">Troubleshooting</a></li>
			</ul>
		</li>	
		<li class="dropdown <? ($SECTION==STILUS_SECTION ? 'active' : '') ?>">
        <a href="#">Spelling, Grammar and Style Proofreading</a>
			<ul class="dropdown-menu">
				<li><a href="stilus.php">Description</a></li>
				<li><a href="stilus.php#seccion2">How to use it?</a></li>
				<li><a href="stilus.php#seccion3">Output</a></li>
				<li><a href="stilus.php#seccion4">Troubleshooting</a></li>
			</ul>
		</li>	
		<li class="dropdown <? ($SECTION==PARSER_SECTION ? 'active' : '') ?>">
        <a href="#">Lemmatization, PoS and Parsing</a>
			<ul class="dropdown-menu">
				<li><a href="parser.php">Description</a></li>
				<li><a href="parser.php#seccion2">How to use it?</a></li>
				<li><a href="parser.php#seccion3">Output</a></li>
				<li><a href="parser.php#seccion4">Troubleshooting</a></li>
			</ul>
		</li>	
		<li class="dropdown <? ($SECTION==TOPICS_SECTION ? 'active' : '') ?>">
        <a href="#">Topics</a>
			<ul class="dropdown-menu">
				<li><a href="topics.php">Description</a></li>
				<li><a href="topics.php#seccion2">How to use it?</a></li>
				<li><a href="topics.php#seccion3">Output</a></li>
				<li><a href="topics.php#seccion4">Troubleshooting</a></li>
			</ul>
		</li>			
		<li class="dropdown <? ($SECTION==SENTIMENT_SECTION ? 'active' : '') ?>">
        <a href="#">Sentiment</a>
			<ul class="dropdown-menu">
				<li><a href="sentiment.php">Description</a></li>
				<li><a href="sentiment.php#seccion2">How to use it?</a></li>
				<li><a href="sentiment.php#seccion3">Output</a></li>
				<li><a href="sentiment.php#seccion4">Troubleshooting</a></li>
			</ul>
		</li>	
	</nav>
    </div>
	
	  <div id="header">
	<a class="download" href="#">Get it now!</a>
	<?php
		if($SECTION!=GENERAL_SECTION){
		echo '<a href="http://textalytics.com/api-core-language-analysis"><img src="http://textalytics.com/img/logo.png" alt="Textalytics"/></a> <em style="margin-left:5px;margin-right:5px;">plug-in for </em><a href="http://gate.ac.uk/"><img src="http://gate.ac.uk/plugins/gau-0.1/images/logo-gate.png" alt="GATE"/></a>';
	}
	?>
		
	</div>
