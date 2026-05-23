<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Active Learning - Login</title>

        <link rel="stylesheet" type="text/css" href="css/login.css">
        <script src="https://www.google.com/recaptcha/api.js" async defer></script>
    </head>

    <body>

        <!-- TOP NAVBAR -->
        <div id="background">
        <div class="top-bar">
            <img src="images/logo-small.png" alt="Logo" class="small-logo">

            <div class="nav-links">
            </div>
        </div>
        <!-- MAIN CONTENT -->

            <img class="twilight" src="images/Twilight.png">
            <img class="layer mountain far" src="images/mountain-far.png">
                    <img class="layer mountain mid" src="images/mountain-mid.png">
                    <img class="layer mountain near" src="images/mountain-near.png">
                    <!-- foreground -->
                    <img class="layer greenery" src="images/greenery2.png">
            <div class="main-container">

                <!-- BIG LOGO -->
                <img src="images/logo-main.png" alt="Active Learning" class="main-logo">


                <!-- LOGIN FORM -->
                <div class="login-container">

                    <h1>LOGIN</h1>
                    <p>WELCOME</p>

                    <form action="CaptchaServlet" method="POST">

                        <div class="input-group">
                            <input type="text" name="username" placeholder="USERNAME" required>
                        </div>

                        <div class="input-group">
                            <input type="password" name="password" id="password" placeholder="PASSWORD" required>
                        </div>

                        <div class="recaptcha-wrap">
                            <div class="g-recaptcha" data-sitekey="6Le2DposAAAAAObHCVLnSeZFyad7TAcx9PN-B9Q_"></div>
                        </div>

                        <button type="submit" class="login-submit-btn">LOGIN</button>

                    </form>
                </div>
            </div>
        </div>

    </body>
</html>
