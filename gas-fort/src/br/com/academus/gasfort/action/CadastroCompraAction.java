package br.com.academus.gasfort.action;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;

import br.com.academus.gasfort.dao.DAODao;
import br.com.academus.gasfort.form.CompraForm;
import br.com.academus.gasfort.modelo.Acao;
import br.com.academus.gasfort.modelo.Compra;
import br.com.academus.gasfort.modelo.FormaPagamento;
import br.com.academus.gasfort.modelo.Fornecedor;
import br.com.academus.gasfort.modelo.Menu;
import br.com.academus.gasfort.modelo.Produto;
import br.com.academus.gasfort.modelo.ProdutoCompra;
import br.com.academus.gasfort.modelo.RegistroAuditoria;
import br.com.academus.gasfort.modelo.Usuario;
import br.com.academus.gasfort.util.HibernateUtil;

public class CadastroCompraAction extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String retorno = "ok";

		HttpSession httpSession = request.getSession();
		Session session = new HibernateUtil().getSession();

		CompraForm formulario = (CompraForm) form;
		DAODao daoDao = new DAODao(session);

		Compra compraAtual = new Compra();

		Fornecedor fornecedor = daoDao.loadFornecedor(formulario.getFornecedor());
		compraAtual.setFornecedor(fornecedor);

		FormaPagamento pagamento = daoDao.loadFormaPagamento(formulario.getFormaPagamento());
		compraAtual.setFormaPagamento(pagamento);

		compraAtual.setData(Calendar.getInstance().getTime());

		daoDao.salva(compraAtual);
		
		RegistroAuditoria registroAuditoria = new RegistroAuditoria();
		
		Usuario usuario = (Usuario) httpSession.getAttribute("user");
		Menu menu = daoDao.loadMenu(Long.parseLong("7"));
		Acao acao = daoDao.loadAcao(Long.parseLong("1"));
		
		registroAuditoria.setUsuario(usuario);
		registroAuditoria.setMenu(menu);
		registroAuditoria.setAcao(acao);
		registroAuditoria.setData(Calendar.getInstance().getTime());
		
		daoDao.salva(registroAuditoria);

		ProdutoCompra produtoCompraAtual = new ProdutoCompra();

		@SuppressWarnings("unchecked")
		List<ProdutoCompra> produtoCompra = (List<ProdutoCompra>) httpSession.getAttribute("produtoCompra");
		
		for (ProdutoCompra produtoCompra2 : produtoCompra) {

			Compra compra = daoDao.loadCompra(compraAtual.getId());
			produtoCompraAtual.setCompra(compra);

			Produto produto = daoDao.loadProduto(produtoCompra2.getProduto().getId());
			produtoCompraAtual.setProduto(produto);
			
			produtoCompraAtual.setValor(produtoCompra2.getValor());

			produtoCompraAtual.setQuantidade(formulario.getQuantidade());

			produtoCompraAtual.setValor_total(formulario.getValor());
			
			daoDao.salva(produtoCompraAtual);

		}

		session.close();

		httpSession.removeAttribute("produtosCompra");
		return mapping.findForward(retorno);

	}
}