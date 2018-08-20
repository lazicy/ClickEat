clickEat.controller('userController', ['$scope', '$location', 'userService', function($scope, $location, userService) {

    function init() {
        console.log('UserController.Init');
        userService.getUserlist().then(
            
            function (response) {
                $scope.userlist = response.data;
            },
            
            function (error) {
                console.log(error);
            }
        
        );
    }

    $scope.goToUserProfile = function(username) {
        $location.path("/profile");
        $location.hash(username);
    }

    init();

 

}]);

