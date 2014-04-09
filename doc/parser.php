<?php
require_once('Config.inc');

$TITLE = 'Lemmatization, PoS and Parsing';
$DESCRIPTION = 'Help page for the GATE Plugin to use Textalytics features inside GATE';
$SECTION = PARSER_SECTION;
$URI = 'http://textalytics.com/core/parser-info';


require_once('Header.inc');
?>
<div id="container">
  <!-- Sección -->
  <a name="seccion1"></a>
  <div class="container section">
    <h1><i class="icon-leaf"></i> Description</h1>
	<p>	Lemmatization, PoS and Parsing is the name of Textalytics' API for the different basic linguistic modules.</p>
	<p>Even though it is simple in name, the parser contains a myriad of functionalities derived from the complete morphosyntactic and semantic analysis it carries out. Instead of including different APIs to obtain all the possible features provided by this analysis, features are grouped in different operating modes, allowing the user to take advantage of as many of them as he wishes and to combine them with other Textalytics' features, such as Topics Extraction.	</p>
  </div>
  
    <!-- Sección -->
  <a name="seccion2"></a>
  <div class="container section">
    <h1> How to use it?</h1>
	<table class="table">
    <caption>Parameter</caption>
    <tbody>
      <tr>
		<td><code>inputASName</code></td>
        <td>Name of the input AnnotationSet which will be used as input. If both this field and <code>inputASTYpes</code> are unset, the whole document will be processed.</td>
      </tr>
	  <tr>
		<td><code>inputASTypes</code></td>
        <td>
			<p>It allows expressions following the pattern: AnnotationType.FeatureName==FeatureValue. Not every element in the pattern must appear.
			For example you may want to specify just Type or Type.FeatureName.
			Only those Annotations that match the pattern will be used as input for the application.</p>
			<p><code>Hint: </code>Consider that this option will take longer time to run because it will process individually the annotations matching the input.</p>
		</td>
      </tr>
	  <tr>
		<td><code>debug</code></td>
        <td>In case of system failure, set it to true to get things logged and <a href="contact.php">send us</a> the log's output.</td>
      </tr>
	  <tr>
		<td><code>inputASName</code></td>
        <td>Name of the output AnnotationSet where new Annotations will be created.</td>
      </tr>
	  <tr>
		<td><code>More details/Other fields</code></td>
        <td><a href="<?php echo $URI;?>">API specificatoin</a></td>
      </tr>
    </tbody>
  </table>
  </div>
  
    <!-- Sección -->
  <a name="seccion3"></a>
  <div class="container section">
    <h1><i class="icon-leaf"></i> Output</h1>
	<p>New Annotations will be created in the AnnotationSet defined by <code>outputASname</code>. 
	The Annotations created follow the structure of the <a href="<?php echo $URI;?>">API specification</a>.
	Sometimes you may find nested objects in the specification. Those objects are flattened using ";" as 1st level separator and "|" as 2nd level separator.
	</p>
  </div>
  
      <!-- Sección -->
  <a name="seccion4"></a>
  <div class="container section">
    <h1><i class="icon-leaf"></i> Troubleshooting</h1>
	<table class="table">
		<tbody>
			<tr>
				<td>This PR uses <a href="https://code.google.com/p/google-gson/downloads/list">google-gson Library</a>. If it doesn't work, please put gson.jar into your %GATE_HOME%/lib folder.</td>
			</tr>
			<tr>
				<td>
					<p>In case of system failure, please set <code>debug</code> to true and <a href="contact.php">send us</a> the log's output.</p>
					<p>If it's not possible for any reason, please just mail us with as much information about the problem as you can provide</p>
				</td>
			</tr>
		</tbody>
	</table>
  </div>
  </div>
  <?php
  require_once('Footer.inc');
  ?>  