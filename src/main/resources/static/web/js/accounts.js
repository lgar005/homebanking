const { createApp } = Vue
console.log("esta funcionando")
const app = createApp( {
    data(){
        return {
             accounts:[ ],
             id:'',
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
                        this.accounts=this.client.accounts                        
                        this.balances=this.accounts.map(account=>account.balance)                        
                        this.numberAccounts=this.accounts.map(account=>account.number)
                        this.totalAccounts=this.balances.reduce((accumulator, currentValue)=>accumulator+=currentValue,0).toLocaleString('en-US', { style: 'currency', currency: 'USD' })                     
                        this.percentages=this.balances.map(balance=>(balance*100)/this.totalAccounts)
                        this.numberLoans=this.client.loans.length
                        this.loans=this.client.loans
                        console.log(this.loans)
                        this.balanceFormat();
                        this.loansFormat();
                        this.loading=false
                        console.log(this.loading)
                        console.log("loands")
                        })
                     }catch{
                        console.log(err)
                     }
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
                createAccount(){
                   /* console.log("crear")
                    axios.post('/api/clients/current/accounts')
                    .then(response =>{
                        window.location.reload();
                    })
                    .cath(console.log("err"))*/
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
                            axios.post('/api/clients/current/accounts')
                            .then(response =>{
                                Swal.fire({
                                    title:'Created!',
                                    text:'Your account has been created.',
                                    icon:'success',
                                    didOpen:()=>{
                                        document.querySelector('.swal2-confirm').addEventListener('click', () =>{location.reload(true)})
                                    }
                                })
                                
                            })
                            .cath(console.log("err")) 
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
                   
        },

    
})
app.mount('#app')
