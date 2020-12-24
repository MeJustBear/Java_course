function hide_table(param){
    let classes = document.getElementsByClassName("subjList");
    if(classes[param].style.display === "table"){
        classes[param].style.display = "none";
        return;
    }else{
        classes[param].style.display = "table";
        return;
    }
}

function hide_button() {
    let but = document.getElementById('ChangeButton');
    if (but !== undefined)
        if (but.style.display === "block") {
            but.style.display = "none";
            return;
        } else {
            but.style.display = "block";
        }
}

function hello(message) {alert(message);}