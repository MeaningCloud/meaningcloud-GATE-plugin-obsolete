<?php
require_once('Config.inc');

$TITLE = 'Spell, Grammar and Style Proofreading';
$DESCRIPTION = 'Help page for the GATE Plugin to use Textalytics features inside GATE';
$SECTION = STILUS_SECTION;
$URI = 'http://textalytics.com/core/stilus-info';


require_once('Header.inc');
?>
<div id="container">
  <!-- Sección -->
  <a name="seccion1"></a>
  <div class="container section">
    <h1><i class="icon-leaf"></i> Description</h1>
	<p>	Spelling, Grammar and Style Proofreading is our solution for automatic proofreading. This module uses multilingual Natural Language Processing technology to check the spelling, grammar and style of your texts with high accuracy, in order to provide precise and up-to-date suggestions and educational explanations based on references. It also provides a number of configuration parameters that allow the user to obtain the corrections in different formats and to customize the behavior for certain types of errors.
	</p>
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
	<p>In case of system failure, please set <code>debug</code> to true and <a href="contact.php">send us</a> the log's output.</p>
	<p>If it's not possible for any reason, please just mail us with as much information about the problem as you can provide</p>
  </div>
  </div>
  <?php
  require_once('Footer.inc');
  ?>  