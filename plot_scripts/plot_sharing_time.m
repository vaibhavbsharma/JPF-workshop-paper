seconds = [ 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 3 8 16 27 52 100 194 387 777 1537 3066 6149];

x_vals=1:30;

hold on;

plot(x_vals,seconds,'r--','Linewidth',4);



set(gca,'FontSize',15); 
h_legend=0; %legend('SPF','Location','northwest');
xlabel('increasing \it{bound}');
ylabel('Time (seconds) to complete exploration');
%xlim([3 51]);
%ylim([0 20]);
set(gca,'FontSize',15);

toplot('increasing \it{bound}','Time (seconds) to complete exploration','');  

hold off;

print -depsc '../figures/sharing_time'
