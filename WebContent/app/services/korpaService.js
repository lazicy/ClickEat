clickEat.service('korpaService', function($http) {
   
    this.getPorudzbineList = function() {
        return $http({  method:'GET', 
                        url:'rest/porudzbine/getPorudzbineList' });
    };

    this.getPorudzbineMap = function() {
        return $http({  method:'GET', 
                        url:'rest/porudzbine/getPorudzbineMap' });
    };

    this.putTrenutneStavke = function(stavke) {
        return $http({  method:'PUT',
                        url:'rest/porudzbine/putTrenutneStavke',
                        data: stavke });
    };

    this.getTrenutneStavkeById = function(id) {
        return $http({  method:'GET',
                        url:'rest/porudzbine/getTrenutneStavkeById/' + id});
    };

    this.makeOrder = function(ptp) {
        return $http({  method:'POST',
                        url:'rest/porudzbine/makeOrder',
                        data: ptp});
    };

    this.clearTrenutneStavke = function() {
        return $http({  method:'GET',
                        url:'rest/porudzbine/clearTrenutneStavke'});
    };

    this.removeFromTrenutneStavke = function(sfr) {
        return $http({  method:'POST',
                        url:'rest/porudzbine/removeFromTrenutneStavke',
                        data: sfr });
    };

    this.deletePorudzbina = function(id) {
        return $http({  method:'DELETE',
                        url:'rest/porudzbine/deletePorudzbina/' + id});
    };

    this.modifyPorudzbina = function(ptm) {
        return $http({  method:'POST',
                        url:'rest/porudzbine/modifyPorudzbina',
                        data: ptm });
    };

    this.dostavi = function(idPorudzbine, usernameDost, usernameKup) {
        return $http({  method:'PUT',
                        url:'rest/porudzbine/dostavi/' + idPorudzbine + "/" + usernameDost + "/" + usernameKup});
    };

    this.expandKorpa = false;

});