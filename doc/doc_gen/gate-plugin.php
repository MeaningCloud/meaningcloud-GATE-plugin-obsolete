<? 
/* Inicio: Configurar página */

/* Fin: Configurar página */

include('header.html');
?>

  		<!-- Documentation sidebar -->
   <div class="span3 bs-docs-sidebar">
      <?php include 'gate-menu.html'  ?>
   </div>
	
  <div class="content">
    <div class="container">
    <div class="row">

     <div class="span9 documentation" >
      <!-- Introduction
      ========================================= -->
			<section id="gt-introduction">
				<?php include 'gate-intro.html' ?>
			</section>	
      <!-- Parameters
      ========================================= -->
			<section id="gt-parameters">
				<?php include 'gate-params.html' ?>
			</section>	
      <!-- APIs
      ========================================= -->
      <section id="gt-apis">
				<h1>Textalytics API</h1>
        <?php include 'gate-lang.html' ?> 
        <?php include 'gate-class.html' ?> 
        <?php include 'gate-topics.html' ?> 
        <?php include 'gate-parser.html' ?> 
        <?php include 'gate-stilus.html' ?> 
        <?php include 'gate-sentiment.html' ?> 
      </section>

      <!-- Download
      ========================================= -->
      <section id="gt-install">
         <?php include 'gate-install.html' ?> 
      </section>

      <!-- Download
      ========================================= -->
      <section id="gt-download">
         <?php include 'gate-download.html' ?> 
      </section>

      <!-- Feedback
      ========================================= -->
      <section id="gt-feedback">
         <?php include 'gate-feedback.html' ?> 
      </section>


     </div>
  </div><!-- row -->

  </div><!-- container -->
</div><!-- content -->

<?php

include('footer.html');

?>
</body>
