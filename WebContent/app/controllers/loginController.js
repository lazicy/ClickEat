clickEat.controller('loginController', ['$scope', '$location', 'userService', function($scope, $location, userService) {

    function init() {
        console.log('LoginController.Init');

        $scope.wrongUsername = false;
        $scope.wrongPassword = false;
        

        userService.getUserOnSession().then(
            
            function(response) {
                if (response.status == 200 ) {
                    userService.activeUser = response.data;
                    console.log("UserOnSession: " + response.data.username);
                    $location.path('/');
                 }
            }, 

            function (error) {
                console.log(error);
            }

        );
    }

    init();

    $scope.login = function(userToLogin) {

        userService.login(userToLogin).then(

            function (response) {

                if (response.status == 200) {
                    if (response.data.errCode === 0) {
                        userService.activeUser = response.data;
                        userService.activeUser.stavke = [];
                        $scope.wrongUsername = false;
                        $scope.wrongPassword = false;
                        $location.path('/');
                    } else if (response.data.errCode === 1) {
                        console.log("username not exist");
                        $scope.wrongUsername = true;
                        $scope.wrongPassword = false;
                    } else if (response.data.errCode === 2) {
                        $scope.wrongUsername = false;
                        $scope.wrongPassword = true;
                    } else {
                        console.log("else: response.data.errCode: " + response.data.errCode);
                    }
                } else {
                    console.log('Neki error.')
                }

            },


            function (error) {
                console.log(error);
            }



        )


    }



}]);