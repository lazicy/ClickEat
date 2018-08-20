clickEat.directive('restoran', function() {
    return {
        restrict: 'E',
        templateUrl: 'directives/restoran_box.html'
    };
});

clickEat.directive('korpa', function() {
    return {
        restrict: 'E',
        templateUrl: 'directives/korpa-mini.html'
    };
});

clickEat.directive('restoranExtended', function() {
    return {
        restrict: 'E',
        templateUrl: 'directives/restoran-extended.html'
    }
});