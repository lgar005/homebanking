const{createApp}= Vue
console.log("hola2")
const app=createApp({
    data(){
        return {
            email: '',
            password:'',
        }
    },
   
    methods:{
        postLogin(){
            axios.post('/api/login',`email=${this.email}&password=${this.password}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(response =>{
                window.location.href='/web/accounts.html'
            })
            .catch(function (error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: 'Incorrect password or email!',
                   
                  })
                
            })
        },
        
    }
})
app.mount('#app')
