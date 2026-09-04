import type {
  AcertoAdmin,
  LancamentoFinanceiro,
  ProcessamentoLog,
  ProdutoEstoque,
  RevendedoraResumo,
} from '../../core/models/admin-data';

export const MOCK_REVENDEDORAS: RevendedoraResumo[] = [
  {
    id: 1,
    nome: 'Camila Ferreira Souza',
    cpf: '519.675.451-20',
    telefone: '(45) 99911-2233',
    saldoDevedor: 1240,
    saldoBonus: 3,
    exposicaoMaleta: 4200,
    diasAtraso: 47,
    dataUltimoAcerto: '28/06/2026',
    loteStatus: 'ABERTO',
  },
  {
    id: 2,
    nome: 'Beatriz Lima Cardoso',
    cpf: '384.221.109-77',
    telefone: '(45) 98844-1102',
    saldoDevedor: 380,
    saldoBonus: 0,
    exposicaoMaleta: 2150,
    diasAtraso: 6,
    dataUltimoAcerto: '05/08/2026',
    loteStatus: 'ABERTO',
  },
  {
    id: 3,
    nome: 'Juliana Pereira Martins',
    cpf: '221.998.330-55',
    telefone: '(45) 99123-4455',
    saldoDevedor: 0,
    saldoBonus: 8,
    exposicaoMaleta: 1890,
    diasAtraso: 0,
    dataUltimoAcerto: '10/08/2026',
    loteStatus: 'ABERTO',
  },
  {
    id: 4,
    nome: 'Fernanda Rodrigues Alves',
    cpf: '109.887.223-44',
    telefone: '(45) 98877-6655',
    saldoDevedor: 920,
    saldoBonus: 0,
    exposicaoMaleta: 3100,
    diasAtraso: 18,
    dataUltimoAcerto: '20/07/2026',
    loteStatus: 'ABERTO',
  },
  {
    id: 5,
    nome: 'Patrícia Gomes Ribeiro',
    cpf: '445.332.110-98',
    telefone: '(45) 99766-8899',
    saldoDevedor: 0,
    saldoBonus: 0,
    exposicaoMaleta: 980,
    diasAtraso: 0,
    dataUltimoAcerto: '02/08/2026',
    loteStatus: 'ABERTO',
  },
  {
    id: 6,
    nome: 'Larissa Costa Nunes',
    cpf: '667.554.221-33',
    telefone: '(45) 98211-3344',
    saldoDevedor: 2480,
    saldoBonus: 0,
    exposicaoMaleta: 5600,
    diasAtraso: 62,
    dataUltimoAcerto: '15/06/2026',
    loteStatus: 'ABERTO',
  },
];

export const MOCK_LANCAMENTOS: Record<number, LancamentoFinanceiro[]> = {
  1: [
    { id: 1, data: '28/06/2026', tipo: 'DEBITO_ACERTO', carteira: 'DINHEIRO', valor: 1240, descricao: 'Acerto — Consignação Nº 4021', saldoApos: 1240 },
    { id: 2, data: '15/05/2026', tipo: 'CREDITO_PAGAMENTO', carteira: 'DINHEIRO', valor: -600, descricao: 'Pagamento em dinheiro', saldoApos: 0 },
    { id: 3, data: '15/05/2026', tipo: 'DEBITO_ACERTO', carteira: 'DINHEIRO', valor: 600, descricao: 'Acerto — Consignação Nº 3987', saldoApos: 600 },
  ],
  6: [
    { id: 4, data: '15/06/2026', tipo: 'DEBITO_ACERTO', carteira: 'DINHEIRO', valor: 2480, descricao: 'Acerto — Consignação Nº 3910', saldoApos: 2480 },
    { id: 5, data: '20/04/2026', tipo: 'AJUSTE_DEBITO', carteira: 'DINHEIRO', valor: 80, descricao: 'Peça danificada — ajuste manual', saldoApos: 80 },
  ],
};

export const MOCK_ACERTOS: Record<number, AcertoAdmin[]> = {
  1: [
    {
      documentoMaleta: '4021',
      dataAcerto: '28/06/2026',
      dataVencimento: '13/08/2026',
      valorVendidoBruto: 2066.67,
      valorComissao: 826.67,
      valorDevido: 1240,
      valorPago: 0,
      status: 'EM_ABERTO',
    },
    {
      documentoMaleta: '3987',
      dataAcerto: '15/05/2026',
      dataVencimento: '30/06/2026',
      valorVendidoBruto: 1000,
      valorComissao: 400,
      valorDevido: 600,
      valorPago: 600,
      status: 'QUITADO',
    },
  ],
};

export const MOCK_PRODUTOS: ProdutoEstoque[] = [
  { codigo: '104994', nome: 'Anel Solitário Prata 925', precoVenda: 189.9, quantidadeDisponivel: 14 },
  { codigo: '105102', nome: 'Colar Ponto de Luz Zircônia', precoVenda: 249.9, quantidadeDisponivel: 6 },
  { codigo: '105330', nome: 'Brinco Argola Semijoia Dourada', precoVenda: 129.9, quantidadeDisponivel: 22 },
  { codigo: '105512', nome: 'Pulseira Riviera Cravejada', precoVenda: 319.9, quantidadeDisponivel: 3 },
  { codigo: '105678', nome: 'Anel Infinito Rosé', precoVenda: 159.9, quantidadeDisponivel: 0 },
  { codigo: '105811', nome: 'Colar Choker Corrente Cubana', precoVenda: 279.9, quantidadeDisponivel: 9 },
  { codigo: '105920', nome: 'Brinco Ponto de Luz Solitário', precoVenda: 99.9, quantidadeDisponivel: 31 },
];

export const MOCK_PROCESSAMENTOS: ProcessamentoLog[] = [
  { id: 1, tipo: 'ACERTO', origem: 'PDF', revendedora: 'Camila Ferreira Souza', dataProcessamento: '28/06/2026', numeroDocumento: '4021', quantidadeItens: 12, valorTotal: 2066.67 },
  { id: 2, tipo: 'CARGA', origem: 'PDF', revendedora: 'Juliana Pereira Martins', dataProcessamento: '10/08/2026', numeroDocumento: '4098', quantidadeItens: 8, valorTotal: 1890 },
  { id: 3, tipo: 'ESTOQUE', origem: 'CSV', revendedora: null, dataProcessamento: '01/08/2026', numeroDocumento: 'ESTQ-0801', quantidadeItens: 340, valorTotal: 0 },
  { id: 4, tipo: 'CARGA', origem: 'MANUAL', revendedora: 'Patrícia Gomes Ribeiro', dataProcessamento: '02/08/2026', numeroDocumento: '4102', quantidadeItens: 5, valorTotal: 980 },
];
