const { createApp } = Vue
const app = createApp( {
    data(){
        return {
            accounts:[ ],
            loans:[ ],
            checked:[ ],
            amount:'',
            loan:'',
            payment:'',
            payments:[ ],
            destinationAccountNumber:'',
            loanInformation:'',
            quota:'',
            amountF:'',
        }
    },
    created(){
        this.getData()
        
    }, 
     methods: {
        getData(){
            axios.all([
                axios.get('/api/clients/current/accounts'),
                axios.get('/api/loans')
            ])
            .then(axios.spread((obj1, obj2) => {
                // Both requests are now complete                
                this.accounts=obj1.data;
                console.log(this.accounts)
                this.loans=obj2.data;
                console.log(this.loans)
                this.balanceFormat()
               
            }))
            .catch(error => console.log(error));    
        },
        applyForLoan(){
            Swal.fire({
                title: 'Are you sure?',
                text: "You want to apply for a loan",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes, apply it!'
              }).then((result) => {
                if (result.isConfirmed) {
                    axios.post('/api/loans',{loanId:this.loan, amount: this.amount, payments:this.payment,destinationAccountNumber:this.destinationAccountNumber })
                    .then(response =>{
                        Swal.fire({
                            title:'Maked!',
                            text:'Your loan has been approved.',
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
        balanceFormat(){
            this.accounts.forEach(element => {
                element.balance = element.balance.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                
            }) 
            this.loans.forEach(loan=>{
                loan.maxAmount=loan.maxAmount.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
            })
        },
        paymentsAccordingLoan(){
            this.loanInformation=this.loans.find(loan=>loan.id==this.loan);
            this.payments=this.loans.find(loan=>loan.id==this.loan).payments
    
           
        },
      
    },
    computed:{
        quotaValue(){  
            this.quota=((this.amount*this.loanInformation.interest)/this.payment).toLocaleString('en-US', { style: 'currency', currency: 'USD' })
            this.amountF=(this.amount*this.loanInformation.interest).toLocaleString('en-US', { style: 'currency', currency: 'USD' })
            console.log(this.quota)
        }
    },
})
app.mount('#app')