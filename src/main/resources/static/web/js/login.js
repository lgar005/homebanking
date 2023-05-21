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
                if(this.email.includes("@bankAdmin")){
                    window.location.href='/web/manager.html'
                }else{
                    window.location.href='/web/accounts.html'
                }
                
                
            })
            .catch(function (error) {
                console.log(error)
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: 'Incorrect password or email!',
                   
                  })
                
            })
        },
        logOut(){
            Swal.fire({
                title: 'Are you sure?',
                text: "Do you want to log out?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes, log out!'
              }).then((result) => {
                if (result.isConfirmed) {
                    axios.post('/api/logout')
                    .then(response =>{
                        window.location.href='/web/index.html'
                    })
                    .cath(console.log("err"))
                }
              })
        },
        
    }
})
app.mount('#app')
