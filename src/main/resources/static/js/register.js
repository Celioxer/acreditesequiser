$(document).ready(function () {
    $('.phone-mask').mask('(00) 00000-0000');

    $('#registerForm').on('input', '#senha, #confirmPassword', function () {
        const senha = $('#senha').val();
        const confirm = $('#confirmPassword').val();
        if (senha && confirm && senha !== confirm) {
            $('#confirmPassword').addClass('error-field');
        } else {
            $('#confirmPassword').removeClass('error-field');
        }
    });
});
