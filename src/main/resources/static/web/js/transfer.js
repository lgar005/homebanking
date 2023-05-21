const { createApp } = Vue
console.log("esta funcionando")

const app = createApp( {
    data(){
        return {
            accounts:[ ],
            checked:[ ],
            amount:'',
            description:'',
            originAccountNumber:'',
            destinationAccountNumber:'',
            ownAccount: 'unselected',
            chooseAD:['Transfer to your own account','Transfer to third party account' ],
            

        }
    },
    created(){
        
        this.getData()
       
        console.log("lo toma")

    }, 
     methods: {
        getData(){
            axios.get('/api/clients/current/accounts')
            .then(response=>{
                this.accounts= response.data.filter(account=>account.active);
                this. balanceFormat()
                console.log(this.accounts)
            })
            .catch(error => console.log(error));    
        },
        makeTransaction(){
            Swal.fire({
                title: 'Are you sure?',
                text: "You are going to make a money transfer",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes, transfer it!'
              }).then((result) => {
                if (result.isConfirmed) {
                    axios.post('/api/transactions',`amount=${this.amount}&description=${this.description}&originAccountNumber=${this.originAccountNumber}&destinationAccountNumber=${this.destinationAccountNumber}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(response =>{
                        Swal.fire({
                            title:'Maked!',
                            text:'Your transfer has been maked.',
                            icon:'success',
                            didOpen:()=>{
                                document.querySelector('.swal2-confirm').addEventListener('click', () =>{window.location.href='/web/transfers.html'})
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
        filterCheck(){
           console.log(this.checked)
            if(this.checked=='Transfer to your own account'){
                this.ownAccount=true;
                console.log("propia cuenta "+ this.ownAccount)
                
            }else if(this.checked=='Transfer to third party account'){
                this.ownAccount=false
                console.log("otra cuenta "+this.ownAccount)
                
            }  
        },
        balanceFormat(){
            this.accounts.forEach(element => {
                element.balance = element.balance.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                
            })
           
        },
    },


})
app.mount('#app')