<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Active Learning - Login</title>

        <link rel="stylesheet" type="text/css" href="css/login.css">
    </head>

    <body>

        <!-- TOP NAVBAR -->
        <div id="background">
        <div class="top-bar">
            <img src="images/logo-small.png" alt="Logo" class="small-logo">

            <div class="nav-links">
                <a href="#">Page 3</a>
                <a href="#">Page 2</a>
                <a href="#">Page 1</a>
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

                    <form action="LoginServlet" method="POST">

                        <div class="input-group">
                            <input type="text" name="username" placeholder="USERNAME" required>

                            <button type="submit" class="icon-btn">
                                <img src="images/enter-button.png" alt="Enter">
                            </button>
                        </div>

                        <div class="input-group">
                            <input type="password" name="password" id="password" placeholder="PASSWORD" required>

                            <button type="button" class="icon-btn" id="togglePassword">
                                <img src="images/clear-button.png" alt="Toggle">
                            </button>
                        </div>

                    </form>
                </div>
            </div>
        </div>

        <script>
            document.getElementById('togglePassword').addEventListener('click', function () {
                var p = document.getElementById('password');
                p.type = p.type === 'password' ? 'text' : 'password';
            });
        </script>
    </body>
</html>
