<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.2.6/jquery.min.js'></script>
  <script type='text/javascript' src='/js/strophe.js'></script>
  <script type='text/javascript' src='/js/echobot.js'></script>
  <link rel="stylesheet" type="text/css" media="all" href="/css/echobot.css" />
  <title>Tweeted Videos</title>
</head>
<body>
  <div id='login'>
    <form name='cred'>
      <label for='jid'>JID:</label>
      <input type='text' id='jid' />
      <label for='pass'>Password:</label>
      <input type='password' id='pass' />
      <input type='button' class='submit' id='connect' value='connect' />
    </form>
  </div>
  <hr />
  <div id='videos'>
    <div id='video'>
      <h2 class='title'><a class='link' target="_blank" href="http://channel4.com/programmes/glee">Glee</a></h2>
      <a class='link' target="_blank" href="http://channel4.com/programmes/glee"><img width="625" height="352" class="image" src="http://www.channel4.com/assets/programmes/images/glee/series-1/episode-17/953dc28f-20d2-48e8-8147-0a74fda872ab_625x352.jpg" /></a>
      <p class='desc''>It's Glee yo!</p>
    </div>
  </div>
  <div id='log'>
  </div>
</body>
</html>
