/**
 * 
 */
package org.ejprd.semanticmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Olamidipupo Ajigboye
 *
 */
@Controller
public class HomeController
{
	@RequestMapping("/semanticmodel")
	public String home(Model model)
	{
			return "index";
	}
}
