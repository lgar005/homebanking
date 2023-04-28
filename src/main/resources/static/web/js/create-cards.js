const { createApp } = Vue
console.log("esta funcionando")
const app = createApp( {
    data(){
        return {
             type:'',
             color:''

        }
    },
     methods: {
         createCard(){
           /* axios.post('/api/clients/current/cards',`type=${this.type}&color=${this.color}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
            .then(response =>{
                  window.setTimeout(window.location.href='/web/cards.html', 5000);
            })
            .catch(function (error) {
                console.log(error)
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: error.response.data,
                   
                  })
            })*/
            Swal.fire({
                title: 'Are you sure?',
                text: "Do you want to acquire a new card?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes, create it!'
              }).then((result) => {
                if (result.isConfirmed) {
                    axios.post('/api/clients/current/cards',`type=${this.type}&color=${this.color}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(response =>{
                        Swal.fire({
                            title:'Created!',
                            text:'Your card has been created.',
                            icon:'success',
                            didOpen:()=>{
                                document.querySelector('.swal2-confirm').addEventListener('click', () =>{window.location.href='/web/cards.html'})
                            }
                        }) 
                    }).catch(function (error) {
                       
                        if(error.response.status==400){
                            Swal.fire({
                                icon: 'error',
                                title: 'Oops...',
                                text: 'All fields must be completed',
                               
                              })
                        }else{
                            Swal.fire({
                                icon: 'error',
                                title: 'Oops...',
                                text: error.response.data,
                               
                              })
                        }
                       
                    })
                  
                }
              })
         },
         logOut(){
            /*axios.post('/api/logout')
            .then(response =>{
                window.location.href='/web/index.html'
            })
            .cath(console.log("err"))*/
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
    },


})
app.mount('#app')
