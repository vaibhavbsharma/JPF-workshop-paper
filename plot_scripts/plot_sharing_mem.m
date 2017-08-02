seconds = [ 240 240 240 240 240 240 240 240 240 240 240 240 240 240 240 240 240 303 556 876 910 910 910 910 910 910 910 910 891 899];

x_vals=1:30;

hold on;

plot(x_vals,seconds,'r--','Linewidth',4);



set(gca,'FontSize',15); 
h_legend=0; %legend('SPF','Location','northwest');
xlabel('increasing \it{bound}');
ylabel('Max memory (MB) reported by SPF');
%xlim([3 51]);
%ylim([0 20]);
set(gca,'FontSize',15);

toplot('increasing \it{bound}','Max memory (MB) reported by SPF','');  

hold off;

print -depsc '../figures/sharing_mem'
