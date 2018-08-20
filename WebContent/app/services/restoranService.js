clickEat.factory('restoranFactory', function($http) {

    var factory = {};

    factory.getRestoranList = function() {
        return $http({  method:'GET',
                        url:'rest/restorani/getRestoranList'});
    };

    factory.getRestoranListList = function() {
        return $http({  method:'GET',
                        url:'rest/restorani/getRestoranListList'});
    };

   
    factory.getRestoranById = function(id) {
        return $http({  method:'GET',
                        url:'rest/restorani/getRestoranById/' + id});
    };
    
    factory.noviRestoran = function(restoran) {
        return $http({  method: 'POST',
                        url:'rest/restorani/noviRestoran',
                        data: restoran });
    };

    factory.modifyRestoran = function(restoran) {
        return $http({  method: 'POST',
                        url:'rest/restorani/modifyRestoran',
                        data: restoran });
    };

    factory.deleteRestoran = function(restoranId) {
        return $http({  method: 'DELETE',
                        url:'rest/restorani/deleteRestoran/' + restoranId});
    };

    factory.getArtikalMap = function() {
        return $http({  method:'GET',
                        url:'rest/artikli/getArtikalMap'});
    };

    factory.getArtikalList = function() {
        return $http({  method:'GET',
                        url:'rest/artikli/getArtikalList'});
    };

    factory.getArtikalById = function(id) {
        return $http({  method:'GET',
                        url:'rest/artikli/getArtikalById/' + id});
    };

    factory.addArtikal = function(artikal) {
        return $http({  method: 'POST',
                        url:'rest/artikli/addArtikal',
                        data: artikal   });
    };

    factory.deleteArtikal = function(id) {
        return $http({  method: 'DELETE',
                        url:'rest/artikli/deleteArtikal/' + id })
    };

    factory.modifyArtikal = function(artikal) {
        return $http({  method: 'POST',
                        url:'rest/artikli/modifyArtikal',
                        data: artikal   });
    };


    factory.addToFavRestorani = function(restoranId, username) {
        return $http({  method:'PUT',
                        url:'rest/restorani/addToFavRestorani/' + restoranId + "/" + username});
    };
    
    factory.removeFromFavRestorani = function(restoranId, username) {
        return $http({  method:'DELETE',    
                        url:'rest/restorani/removeFromFavRestorani/' + restoranId + "/" + username});
    };

    factory.restoranList = [];

    factory.loadRestoranList = function() {
        factory.getRestoranList().then(
            function(response) {
                if (response.status == 200) {
                    factory.restoranList = response.data;
                } 
            }, function(error) {
                factory.restoranList = [];
            }
        );
    }
    

    return factory;

});