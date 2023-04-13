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
             numberLoans:0

        }
    },
    created(){
            this.getData()

    },
     methods: {
         async getData(){
                    try{
                        this.params=new URLSearchParams(location.search)
                        this.id= this.params.get("id");  
                        console.log(this.id) 
                        axios.get('http://localhost:8080/api/clients/'+ this.id)
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
                balanceFormat(){
                    this.accounts.forEach(element => {
                        element.balance = element.balance.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                        element.creationDate=element.creationDate.toString().replace('T', ' ')
                        
                    })
                },
                loansFormat(){
                    this.loans.forEach(loan=>{
                        loan.amount=loan.amount.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                    })
                }    
        },

    computed : {


    }
})
app.mount('#app')
