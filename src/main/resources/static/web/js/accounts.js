const { createApp } = Vue
console.log("esta funcionando")
const app = createApp( {
    data(){
        return {
             accounts:[ ],
             //id:'',
             params:'',
             client:'',
             clients:[ ],
             balances:[ ],
             numberAccounts:[ ],
             totalAccounts:0,
             percentages:[ ],
             loans:[ ],
             loading:true,
             numberLoans:0,
             accountType:'',
             idclientLoan:'',
             numberAccount:'',
             amount:'',
             quota:'',
             quotaString:'',
             loanSelect:''
        }
    },
    created(){
            this.getData()
    },
     methods: {
         async getData(){
                    try{
                     
                        axios.get('/api/clients/current')
                        .then(elemento => {    
                        console.log(elemento.data)                   
                        this.client=elemento.data                     
                        this.accounts=this.client.accounts.filter(account=>account.active)
                        console.log(this.accounts)                        
                        this.balances=this.accounts.map(account=>account.balance)                        
                        this.numberAccounts=this.accounts.map(account=>account.number)
                        this.totalAccounts=this.balances.reduce((accumulator, currentValue)=>accumulator+=currentValue,0).toLocaleString('en-US', { style: 'currency', currency: 'USD' })                     
                        this.percentages=this.balances.map(balance=>(balance*100)/this.totalAccounts)
                        this.numberLoans=this.client.loans.length
                        this.loans=this.client.loans.filter(loan=>loan.amountF>0)
                        console.log(this.loans)
                        this.balanceFormat();
                        this.loansFormat();
                        this.loading=false
                        //console.log(this.loading)
                        //console.log("loands")
                        })
                     }catch{
                        console.log(err)
                     }
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
                createAccount(){
                    Swal.fire({
                        title: 'Are you sure?',
                        text: "Do you want to create a new account?",
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#3085d6',
                        cancelButtonColor: '#d33',
                        confirmButtonText: 'Yes, create it!'
                      }).then((result) => {
                        if (result.isConfirmed) {
                            axios.post('/api/clients/current/accounts', `accountType=${this.accountType}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                            .then(response =>{
                                Swal.fire({
                                    title:'Created!',
                                    text:'Your account has been created.',
                                    icon:'success',
                                    didOpen:()=>{
                                        document.querySelector('.swal2-confirm').addEventListener('click', () =>{location.reload(true)})
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
                deleteAccount(number){
                    Swal.fire({
                        title: 'Are you sure?',
                        text: "Do you want to delete a account?",
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#3085d6',
                        cancelButtonColor: '#d33',
                        confirmButtonText: 'Yes, delete it!'
                      }).then((result) => {
                        if (result.isConfirmed) {
                            axios.patch('/api/clients/current/accounts',`accountNumber=${number}`)
                            .then(response =>{
                                Swal.fire({
                                    title:'Deleted!',
                                    text:'Your account has been deleted.',
                                    icon:'success',
                                    didOpen:()=>{
                                        document.querySelector('.swal2-confirm').addEventListener('click', () =>{location.reload(true)})
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
                payLoan(id){
                    console.log(id);
                    Swal.fire({
                        title: 'Are you sure?',
                        text: "You want to pay a loan",
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#3085d6',
                        cancelButtonColor: '#d33',
                        confirmButtonText: 'Yes, pay it!'
                      }).then((result) => {
                        if (result.isConfirmed) {
                            axios.post('/api/clients/current',`idclientLoan=${id}&numberAccount=${this.numberAccount}&amount=${this.quota}`)
                            .then(response =>{
                                Swal.fire({
                                    title:'Maked!',
                                    text:'Your loan has been paid.',
                                    icon:'success',
                                    didOpen:()=>{
                                        document.querySelector('.swal2-confirm').addEventListener('click', () =>{window.location.href='/web/accounts.html'})
                                    }
                                }) 
                            }).catch(function (error) {
                                if(error.response.status==400 || error.response.status==500){
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
                balanceFormat(){
                    this.accounts.forEach(element => {
                        element.balance = element.balance.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                        //element.creationDate=element.creationDate.toString().replace('T', ' ')
                        element.creationDate=element.creationDate.slice(0,10)
                        
                    })
                },
                loansFormat(){
                    this.loans.forEach(loan=>{
                        loan.amount=loan.amount.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                        
                    })

                },
                quotaValue(id){ 
                    this.loanSelect= this.loans.find(loan=>loan.id==id); 
                    this.quota=(this.loanSelect.amountF/this.loanSelect.payments)
                    this.quotaString=this.quota.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                }
                   
        },

    
})
app.mount('#app')
/*const navLinksEls=document.querySelectorAll('nav-link');
navLinksEls.forEach(navLinksEl=>{
    navLinksEl.addEventListener('click',()=>{
        document.querySelector('active')?.classList.remove('active');
        navLinksEl.classList.add('active');
    })
})*/
const activePage = window.location.pathname;
console.log(activePage)
const navLinks = document.querySelectorAll('nav-link').forEach(link => {
  if(link.href.includes(`${activePage}`)){
    link.classList.add('active');
    console.log(link);
  }
})