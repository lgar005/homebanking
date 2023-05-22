const{createApp}= Vue
console.log("hola2")
const app=createApp({
    data(){
        return {
            id:'',
            params:'',
            account:'',
            accounts:[ ],
            transactions:[ ],
            initialDate:'',
            finalDate:''
           
        }
    },
    created(){
        
        this.getData()

    }, 
    methods:{
        async getData(){
            try{
                this.params=new URLSearchParams(location.search)
                this.id= this.params.get("id")
                console.log(this.id)
                axios.get('http://localhost:8080/api/accounts/'+ this.id)
                .then(elemento => {
                    console.log(elemento.data)
                    this.account=elemento.data
                    this.transactions=this.account.transactions.sort((x,y)=>y.id-x.id).filter(account=>account.active)
                    this.amountFormat();
                    this.account.balance=this.account.balance.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                    this.account.creationDate=this.account.creationDate.slice(0,10);
                })
            }catch(err){
                console.log(err)
            }
        },
        downloadPdf(){
          Swal.fire({
            title: 'Are you sure?',
            text: "Do you want to download the transaction pdf?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, download it!'
          }).then((result) => {
            if (result.isConfirmed) {
                axios.post('/api/download',`idAccount=${this.id}&initialDate=${this.initialDate}&finalDate=${this.finalDate}`)
                .then(response =>{
                    Swal.fire({
                        title:'Downloaded!',
                        text:'Your transfer has been downloaded.',
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
                            text: 'Please select both dates ',
                           
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
        amountFormat(){
            this.transactions.forEach(element => {
                element.amount = element.amount.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                element.data=element.data.toString().replace('T', ' ')
            })
           
        },
       
    },
    
})
app.mount('#app')
/*computed:{
        dates(){
            $(document).ready(function(){
                // Test Moment.js
                var clock = setInterval(function() {
                  $('#clock').text(moment().format('YYYY-MM-DD HH:mm:ss'))
                }, 1000);
                
                // Test DataTables.js
                $.fn.dataTable.ext.errMode = 'throw';
                
                $.fn.dataTable.ext.search.push(
                  function( settings, data, dataIndex, dataFull, dataFullIndex ) {
                    var wrapper       = $(settings.nTable).closest('.table-wrapper');
                    var fecha_row     = dataFull.Data.timestamp;
                    var fecha_inicio  = $(wrapper).find('[name="fecha_inicio"]').val();
                    var fecha_fin     = $(wrapper).find('[name="fecha_fin"]').val();
                    fecha_inicio      = moment(fecha_inicio, 'YYYY-MM-DD').unix();
                    fecha_fin         = moment(fecha_fin, 'YYYY-MM-DD').unix();
                    console.log(fecha_row, fecha_inicio, fecha_fin);
                    
                    if (( ! isNaN(fecha_inicio) && fecha_inicio > fecha_row) ||
                        ( ! isNaN(fecha_fin)    && fecha_fin    < fecha_row)) {
                      return false;
                    }
                    return true;
                    
                  }
                );
                
               $('#table1 table').DataTable({
                  dom: 'ti',
                  order: [],
                  columns: [
                    {
                      name: 'Id',
                      data: 'Id'
                    },
                    {
                      name: 'Type',
                      data: 'Type'
                    },
                    {
                        name: 'Data',
                        data: 'Data',
                    },
                    {
                        name: 'Description',
                        data: 'Description',
                    },
                    {
                        name: 'Amount',
                        data: 'Amount',
                    },
                    {
                      name: 'Balance',
                      data: 'Balance',
                      render: {
                          _: 'display',
                          sort: 'timestamp',
                          filter: 'timestamp'
                      }
                    }
                  ]
                });
                
                $('#table1 table').DataTable().rows.add([
                  {
                    nombre: 'Eva',
                    apellido: 'Tena',
                    fecha_nacimiento: {
                        display: "25 / 04 / 1992",
                        timestamp: moment("25-04-1992", "DD-MM-YYYY").unix() // "704152800"
                    },
                  },
                  {
                    nombre: 'Arturo',
                    apellido: 'Garcia',
                    fecha_nacimiento: {
                        display: "03 / 05 / 1992",
                        timestamp: moment("03-05-1992", "DD-MM-YYYY").unix() // "704844000"
                    },
                  },
                  {
                    nombre: 'Rosa',
                    apellido: 'Perez',
                    fecha_nacimiento: {
                        display: "04 / 03 / 1993",
                        timestamp: moment("04-03-1993", "DD-MM-YYYY").unix() // "731199600"
                    },
                  }
                ]).draw();
              
                
                $(document).on('input', '.filtro input', function(e) {
                  var wrapper = $(this).closest('.table-wrapper');
                  var fecha_inicio = $(wrapper).find('[name="fecha_inicio"]').val();
                  var fecha_fin = $(wrapper).find('[name="fecha_fin"]').val();
                  
                  // Evitar que la fecha inicio sea posterior a la fecha fin.
                  if (moment(fecha_inicio).isAfter(fecha_fin)) {
                    setTimeout(function() {
                      $(wrapper).find('[name="fecha_fin"]').val(fecha_inicio);  
                    }, 1000);
                  }
                  
                  $(wrapper).find('table').DataTable().draw();
                });
                
                
              });
        }
    }*/