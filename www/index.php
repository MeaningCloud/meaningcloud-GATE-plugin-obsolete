<?php
require_once('Config.inc');

$TITLE = 'Text Classification';
$DESCRIPTION = 'Help page for the GATE Plugin to use Textalytics features inside GATE';
$SECTION = GENERAL_SECTION;
$URI = 'http://textalytics.com/core/class-info';


require_once('Header.inc');
?>
<div id="container">
<div class="texta" style="width:40%;float:left;margin-left:25px;">
<a href="http://textalytics.com/api-core-language-analysis"><img src="http://textalytics.com/img/logo.png" alt="Textalytics"/></a>
<p style="font-size:150%">If you need for your texts a sentiment analysis able to recognize irony and subjectivity, extract themes and concepts, establish semantic relations among concepts, detect the language or classify automatically according to a class model, Core API is your solution. 
Choose your <a href="http://textalytics.com/api-core-language-analysis">API!</a></p>
</div>
<div class="gate" style="width:40%;float:right;margin-right:25px;">
<a href="http://gate.ac.uk/"><img src="http://gate.ac.uk/plugins/gau-0.1/images/logo-gate.png" alt="GATE"/></a>
<p style="font-size:150%">General Architecture for Text Engineering or GATE is a Java suite of tools originally developed at the University of Sheffield beginning in 1995 and now used worldwide by a wide community of scientists, companies, teachers and students for all sorts of natural language processing tasks, including information extraction in many languages.</p>
</div>
</div>
<?php
  require_once('Footer.inc');
?>