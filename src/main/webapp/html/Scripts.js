function hide_table(param){
    var classes = document.getElementsByClassName("subjList");
    if(classes[param].style.display === "table"){
        classes[param].style.display = "none";
        return;
    }else{
        classes[param].style.display = "table";
        return;
    }
}