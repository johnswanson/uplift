set nocompatible
" Setting up Vundle - the vim plugin bundler
    let iCanHazVundle=1
    let vundle_readme=expand('~/.vim/bundle/vundle/README.md')
    if !filereadable(vundle_readme)
        echo "Installing Vundle.."
        echo ""
        silent !mkdir -p ~/.vim/bundle
        silent !git clone https://github.com/gmarik/vundle ~/.vim/bundle/vundle
        let iCanHazVundle=0
    endif
    set rtp+=~/.vim/bundle/vundle/
    call vundle#rc()
    "Add your bundles here
    "Bundle 'tpope/vim-fugitive'
    Bundle 'FredKSchott/CoVim'
    Bundle 'gmarik/vundle'
    Bundle 'kien/ctrlp.vim'
    Bundle 'pangloss/vim-javascript'
    Bundle 'tpope/vim-surround'
    Bundle 'kchmck/vim-coffee-script'
    Bundle 'derekwyatt/vim-scala'
    Bundle 'altercation/vim-colors-solarized'
    Bundle 'vim-scripts/VimClojure'
    Bundle 'vim-scripts/JSON.vim'
    Bundle 'goldfeld/vim-seek'
    Bundle 'vim-scripts/vimwiki'
    Bundle 'benmills/vimux'
    if iCanHazVundle == 0
        echo "Installing Bundles, please ignore key map error messages"
        echo ""
        :BundleInstall
    endif
" Setting up Vundle - the vim plugin bundler end
"
filetype plugin indent on
syntax on
let loaded_matchparen = 1
"let g:ctrlp_clear_cache_on_exit = 1
let g:ctrlp_cache_dir = $HOME.'/.cache/ctrlp'
let go_highlight_trailing_whitespace_error=0
color solarized
set lsp=5
set background=dark
set clipboard=unnamed
set hidden
set autoindent
set smartindent
set gdefault
set smarttab
set ignorecase
set smartcase
set incsearch
set hlsearch
set history=2000
set undolevels=2000
set undoreload=2000
set title
set vb t_vb=
set showmatch
set noerrorbells
set nuw=1
set backspace=indent,eol,start
set shiftwidth=2
set tabstop=2
set expandtab
let mapleader = ","
nnoremap <Leader>g :GundoToggle<CR>
nnoremap <S-CR> O<Esc>j
nnoremap <CR> o<Esc>k
noremap <C-h> <C-w>h
noremap <C-j> <C-w>j
noremap <C-k> <C-w>k
noremap <C-l> <C-w>l
nnoremap / /\v
nnoremap <Leader><CR> i<CR><Esc>
nnoremap <silent> <Leader>a :nohl<CR>
nnoremap <Leader>/ :Ack 
nnoremap ; :
nnoremap J mlJ`l
nnoremap <Leader>0 :VimuxPromptCommand<CR>
nnoremap <Leader>t :VimuxRunLastCommand<CR>

" check off and uncheck items on todo list
nnoremap <Leader>d ^rxj
nnoremap <Leader>s ^roj
set scrolloff=2
set noswapfile
set nowb
set nobackup
set viminfo^=%
set linespace=0
set shortmess=aOstT
set undofile
set undodir=~/.vimundo//
set rnu
set wildignore=*/.git/*,*/templates_c/*,*/live/*
set shortmess+=I
set splitbelow
set splitright
set encoding=utf-8
let g:vimwiki_dir_link = "index"
let g:vimwiki_list = [{'path': '~/notes/wiki/',
                       \ 'syntax': 'markdown', 
                       \'ext': '.md'}]
set colorcolumn=80
