$(document).ready(function(){
    $("#addIngredientGroupButton").click(function() {
        var selection = $(this).prev().children().first().children().first();

        var newSelectId = inputGroupSelect + ($("select").length + 1);

        var inputGroup = selection.parent().parent().last().clone();
        var newSelection = inputGroup.children().first().children().first();
        var newInput = inputGroup.children().first().children().last();

        newSelection.attr("id", newSelectId);

        selection.parent().parent().last().after(inputGroup);
    });
});
function removeIngredient(button) {
    alert("removing");
    var list = document.getElementsByTagName("select");
    if (list.length > 1){
        button.parentElement.remove();
    }
};

function processDigits(input){
    let value = input.value;
    let numbers = value.replace(/[^0-9]/g, "");
    input.value = numbers;
}

function processLetters(input){
    let value = input.value;
    let letters = value.replace(/[0-9]/g, "");
    input.value = letters;
}