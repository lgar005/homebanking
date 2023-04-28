const{createApp}= Vue
console.log("hola2")
const app=createApp({
    data(){
        return {
            firstName:'',
            lastName:'',
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
                console.log(error.toJSON());
            })
        },
        register(){
            axios.post('/api/clients',`firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(()=>{
                this.postLogin()
            })
            .catch(function (error) {
                console.log(error)
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: error.response.data,
                   
                  })
            })
                
        },
        
    }
})
app.mount('#app')