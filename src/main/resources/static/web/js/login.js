const{createApp}= Vue
console.log("hola2")
const app=createApp({
    data(){
        return {
            email: '',
            password:''
        }
    },
   
    methods:{
        postLogin(){
            axios.post('/api/login',`email=${this.email}&password=${this.password}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(response =>{
                window.location.href='/web/accounts.html'
            })
            .catch(function (error) {
                console.log(error.toJSON().status);
            })
          },
        
    }
})
app.mount('#app')
/**
 * Change the error message
 */
document.getElementById("email").onchange = function() {
    this.setCustomValidity('');
};
document.getElementById("email").oninvalid = function() {
    this.setCustomValidity("Merci d'indiquer votre adresse email!");
};